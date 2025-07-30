package info.smart_tools.smartactors.scheduler_plugins.scheduler_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.FunctionExecutionException;
import info.smart_tools.smartactors.base.interfaces.ipool.IPool;
import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.scheduler.actor.SchedulerActor;
import info.smart_tools.smartactors.scheduler.actor.impl.EntryImpl;
import info.smart_tools.smartactors.scheduler.actor.impl.EntryStorage;
import info.smart_tools.smartactors.scheduler.actor.impl.actions.BlockingMessageSchedulerAction;
import info.smart_tools.smartactors.scheduler.actor.impl.actions.DefaultSchedulerAction;
import info.smart_tools.smartactors.scheduler.actor.impl.filter.SchedulerPreShutdownModeEntryFilter;
import info.smart_tools.smartactors.scheduler.actor.impl.refresher.EntryStorageRefresher;
import info.smart_tools.smartactors.scheduler.actor.impl.refresher.ISchedulerStorageRefresher;
import info.smart_tools.smartactors.scheduler.actor.impl.remote_storage.DatabaseRemoteStorage;
import info.smart_tools.smartactors.scheduler.actor.impl.remote_storage.IRemoteEntryStorage;
import info.smart_tools.smartactors.scheduler.actor.impl.remote_storage.NullRemoteStorage;
import info.smart_tools.smartactors.scheduler.actor.impl.service.SchedulingService;
import info.smart_tools.smartactors.scheduler.actor.impl.timer.SchedulerTimer;
import info.smart_tools.smartactors.scheduler.interfaces.IDelayedSynchronousService;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerEntryStorage;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerEntryStorageObserver;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerService;
import info.smart_tools.smartactors.timer.interfaces.itimer.ITimer;

/**
 * Plugin that registers scheduler actor and related components.
 */
public class PluginScheduler extends BootstrapPlugin {
    /**
     * The constructor.
     *
     * @param bootstrap    the bootstrap
     */
    public PluginScheduler(final IBootstrap bootstrap) {
        super(bootstrap);
    }

    /**
     * Register default action to be executed by scheduler when entry fires.
     *
     * @throws ResolutionException if error occurs resolving key
     * @throws RegistrationException if error occurs registering strategy
     * @throws InvalidArgumentException if {@link ApplyFunctionToArgumentsStrategy} does not like our function
     */
    @Item("scheduler_default_action")
    public void registerDefaultAction()
            throws ResolutionException, RegistrationException, InvalidArgumentException {
        IStrategy nonBlockingMessageActionS = new SingletonStrategy(new DefaultSchedulerAction());
        IStrategy blockingMessageActionS = new SingletonStrategy(new BlockingMessageSchedulerAction());

        IOC.register(
                Keys.getKeyByName("blocking message scheduler action"),
                blockingMessageActionS
        );
        IOC.register(
                Keys.getKeyByName("non-blocking scheduler action"),
                nonBlockingMessageActionS
        );
        IOC.register(
                Keys.getKeyByName("default scheduler action"),
                nonBlockingMessageActionS
        );
    }

    /**
     * Register scheduler entry creation and restoration strategies.
     *
     * @throws ResolutionException if error occurs resolving key
     * @throws RegistrationException if error occurs registering strategy
     * @throws InvalidArgumentException if {@link ApplyFunctionToArgumentsStrategy} does not like our function
     */
    @Item("scheduler_entry_strategies")
    public void registerEntry()
            throws ResolutionException, RegistrationException, InvalidArgumentException {
        IOC.register(
                Keys.getKeyByName("new scheduler entry"),
                new ApplyFunctionToArgumentsStrategy(args -> {
                    try {
                        return EntryImpl.newEntry((IObject) args[0], (ISchedulerEntryStorage) args[1]);
                    } catch (Exception e) {
                        throw new FunctionExecutionException(e);
                    }
                })
        );
        IOC.register(
                Keys.getKeyByName("restore scheduler entry"),
                new ApplyFunctionToArgumentsStrategy(args -> {
                    try {
                        return EntryImpl.restoreEntry((IObject) args[0], (ISchedulerEntryStorage) args[1]);
                    } catch (Exception e) {
                        throw new FunctionExecutionException(e);
                    }
                })
        );
    }

    private static final long DEFAULT_BASE_REFRESH_INTERVAL = 1000 * 10; // 10 seconds
    private static final int DEFAULT_REFRESH_PAGE_SIZE_MAX = 100;
    private static final int DEFAULT_REFRESH_PAGE_SIZE_MIN = 20;
    private static final int DEFAULT_REFRESH_LOCAL_ENTRIES = 1000;

    /**
     * Register default refresh parameters resolution strategies.
     *
     * <p>
     * By-default constant refresh intervals and page size are set but the strategies may be replaced.
     * </p>
     *
     * @throws ResolutionException if error occurs resolving a key
     * @throws RegistrationException if error occurs registering a strategy
     * @throws InvalidArgumentException if {@link SingletonStrategy} does not accept default values
     */
    @Item("scheduler_storage_refresh_parameters:default")
    public void registerDefaultRefreshIntervals()
            throws ResolutionException, RegistrationException, InvalidArgumentException {
        // RRI = BASE_INTERVAL sec
        IOC.register(Keys.getKeyByName("scheduler storage refresh interval: rri"),
                new SingletonStrategy(DEFAULT_BASE_REFRESH_INTERVAL));
        // RAI = 1.5 * BASE_INTERVAL sec
        IOC.register(Keys.getKeyByName("scheduler storage refresh interval: rai"),
                new SingletonStrategy(DEFAULT_BASE_REFRESH_INTERVAL + DEFAULT_BASE_REFRESH_INTERVAL / 2));
        // RSI = 2 * BASE_INTERVAL sec
        IOC.register(Keys.getKeyByName("scheduler storage refresh interval: rsi"),
                new SingletonStrategy(DEFAULT_BASE_REFRESH_INTERVAL * 2));

        IOC.register(Keys.getKeyByName("scheduler storage refresh max page size"),
                new SingletonStrategy(DEFAULT_REFRESH_PAGE_SIZE_MAX));

        IOC.register(Keys.getKeyByName("scheduler storage refresh min page size"),
                new SingletonStrategy(DEFAULT_REFRESH_PAGE_SIZE_MIN));

        IOC.register(Keys.getKeyByName("scheduler storage refresh local entries limit"),
                new SingletonStrategy(DEFAULT_REFRESH_LOCAL_ENTRIES));
    }

    /**
     * Register strategy of creation of a refresher service for entry storage.
     *
     * @throws ResolutionException if error occurs resolving key
     * @throws RegistrationException if error occurs registering strategy
     * @throws InvalidArgumentException if {@link ApplyFunctionToArgumentsStrategy} does not like our function
     */
    @Item("scheduler_entry_storage_refresher")
    @After({
        "scheduler_entry_strategies",
        "scheduler_storage_refresh_parameters:default",
    })
    public void registerRefresher()
            throws ResolutionException, RegistrationException, InvalidArgumentException {
        IOC.register(Keys.getKeyByName("scheduler entry storage refresher"), new ApplyFunctionToArgumentsStrategy(args -> {
            try {
                EntryStorage storage = (EntryStorage) args[0];
                IRemoteEntryStorage remoteEntryStorage = (IRemoteEntryStorage) args[1];

                long rrInterval = IOC.resolve(Keys.getKeyByName("scheduler storage refresh interval: rri"), args);
                long raInterval = IOC.resolve(Keys.getKeyByName("scheduler storage refresh interval: rai"), args);
                long rsInterval = IOC.resolve(Keys.getKeyByName("scheduler storage refresh interval: rsi"), args);
                int refreshPageSize = IOC.<Number>resolve(Keys.getKeyByName("scheduler storage refresh max page size"), args).intValue();
                int refreshMinPageSize = IOC.<Number>resolve(Keys.getKeyByName("scheduler storage refresh min page size"), args).intValue();
                int maxLocalEntries = IOC.<Number>resolve(Keys.getKeyByName("scheduler storage refresh local entries limit"), args).intValue();
                return new EntryStorageRefresher(
                        storage, remoteEntryStorage, rrInterval, raInterval, rsInterval, refreshPageSize, refreshMinPageSize, maxLocalEntries);
            } catch (ClassCastException | ResolutionException e) {
                throw new FunctionExecutionException(e);
            }
        }));
    }

    /**
     * Register strategy of creation of a timer service.
     *
     * @throws ResolutionException if error occurs resolving key
     * @throws RegistrationException if error occurs registering strategy
     * @throws InvalidArgumentException if {@link ApplyFunctionToArgumentsStrategy} does not like our function
     */
    @Item("timer_service")
    public void registerTimerService()
            throws ResolutionException, RegistrationException, InvalidArgumentException {
        IOC.register(Keys.getKeyByName("new timer service"), new ApplyFunctionToArgumentsStrategy(args -> {
            try {
                ITimer timer = (ITimer) args[0];
                return new SchedulerTimer(timer);
            } catch (ResolutionException e) {
                throw new FunctionExecutionException(e);
            }
        }));
    }

    /**
     * Register scheduler service creation strategy.
     *
     * @throws ResolutionException if error occurs resolving key
     * @throws RegistrationException if error occurs registering strategy
     * @throws InvalidArgumentException if {@link ApplyFunctionToArgumentsStrategy} does not like our function
     */
    @Item("scheduler_service")
    @After({
        "scheduler_entry_storage_refresher",
        "timer_service",
    })
    public void registerStorage()
            throws ResolutionException, RegistrationException, InvalidArgumentException {
        IOC.register(Keys.getKeyByName("new scheduler service"), new ApplyFunctionToArgumentsStrategy(args -> {
            try {
                ISchedulerEntryStorageObserver observer = (args.length > 2) ? (ISchedulerEntryStorageObserver) args[2] : null;
                IRemoteEntryStorage remoteEntryStorage = new DatabaseRemoteStorage((IPool) args[0], (String) args[1]);

                Object systemTimer = IOC.resolve(Keys.getKeyByName("timer"));
                IDelayedSynchronousService timerService = IOC.resolve(Keys.getKeyByName("new timer service"), systemTimer);

                EntryStorage storage = new EntryStorage(remoteEntryStorage, observer, (ITimer) timerService);

                ISchedulerStorageRefresher refresher = IOC.resolve(
                        Keys.getKeyByName("scheduler entry storage refresher"),
                        storage, remoteEntryStorage);

                return new SchedulingService(timerService, refresher, storage);
            } catch (ResolutionException e) {
                throw new FunctionExecutionException(e);
            }
        }));
        IOC.register(Keys.getKeyByName("local only scheduler entry storage"), new ApplyFunctionToArgumentsStrategy(args -> {
            try {
                ISchedulerEntryStorageObserver observer = (args.length > 0) ? (ISchedulerEntryStorageObserver) args[0] : null;
                return new EntryStorage(NullRemoteStorage.INSTANCE,  observer);
            } catch (ResolutionException e) {
                throw new FunctionExecutionException(e);
            }
        }));
    }

    /**
     * Register default scheduler service activation action for scheduler actor -- do nothing.
     *
     * @throws ResolutionException if error occurs resolving key
     * @throws RegistrationException if error occurs registering strategy
     * @throws InvalidArgumentException if {@link SingletonStrategy} does not like our function
     */
    @Item("default_scheduler_actor_service_activation_action")
    public void registerDefaultActivationAction()
            throws ResolutionException, RegistrationException, InvalidArgumentException {
        IOC.register(Keys.getKeyByName("scheduler service activation action for scheduler actor"),
                new SingletonStrategy((IAction<ISchedulerService>) service -> { }));
    }

    /**
     * Register {@link info.smart_tools.smartactors.scheduler.interfaces.ISchedulerEntryFilter entry filter} for pre-shutdown scheduler
     * mode.
     *
     * @throws ResolutionException if error occurs resolving key or dependencies of a filter
     * @throws RegistrationException if error occurs registering strategy
     * @throws InvalidArgumentException if {@link SingletonStrategy} does not like our function
     */
    @Item("scheduler_pre_shutdown_mode_entry_filter")
    public void registerPreShutdownModeEntryFilter()
            throws ResolutionException, InvalidArgumentException, RegistrationException {
        IOC.register(Keys.getKeyByName("pre shutdown mode entry filter"),
                new SingletonStrategy(new SchedulerPreShutdownModeEntryFilter()));
    }

    /**
     * Register scheduler actor creation strategy.
     *
     * @throws ResolutionException if error occurs resolving key
     * @throws RegistrationException if error occurs registering strategy
     * @throws InvalidArgumentException if {@link ApplyFunctionToArgumentsStrategy} does not like our function
     */
    @Item("scheduler_actor")
    @After({
        "scheduler_service",
        "scheduler_entry_strategies",
        "default_scheduler_actor_service_activation_action",
        "scheduler_pre_shutdown_mode_entry_filter",
    })
    public void registerActor()
            throws ResolutionException, RegistrationException, InvalidArgumentException {
        IOC.register(Keys.getKeyByName("scheduler actor"), new ApplyFunctionToArgumentsStrategy(args -> {
            try {
                return new SchedulerActor((IObject) args[0]);
            } catch (Exception e) {
                throw new FunctionExecutionException(e);
            }
        }));
    }
}
