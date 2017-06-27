package info.smart_tools.smartactors.scheduler.actor.impl;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerAction;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerEntry;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerEntryStorage;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulingStrategy;
import info.smart_tools.smartactors.scheduler.interfaces.exceptions.SchedulingStrategyExecutionException;
import info.smart_tools.smartactors.scheduler.interfaces.exceptions.EntryScheduleException;
import info.smart_tools.smartactors.scheduler.interfaces.exceptions.EntryStorageAccessException;
import info.smart_tools.smartactors.scheduler.interfaces.exceptions.SchedulerActionExecutionException;
import info.smart_tools.smartactors.scheduler.interfaces.exceptions.SchedulerActionInitializationException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import info.smart_tools.smartactors.task.interfaces.itask.ITask;
import info.smart_tools.smartactors.task.interfaces.itask.exception.TaskExecutionException;
import info.smart_tools.smartactors.timer.interfaces.itimer.ITimerTask;
import info.smart_tools.smartactors.timer.interfaces.itimer.exceptions.TaskScheduleException;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Implementation of {@link ISchedulerEntry}.
 */
public final class EntryImpl implements ISchedulerEntry {
    private static final String DEFAULT_ACTION_KEY_NAME = "default scheduler action";

    private final ISchedulerEntryStorage storage;
    private final IObject state;
    private final String id;
    private final ISchedulingStrategy strategy;
    private long lastScheduledTime;
    private final AtomicReference<ITimerTask> timerTask;
    private boolean isCancelled;
    private boolean isSavedRemotely;
    private final ISchedulerAction action;
    private final ITask task = this::executeTask;

    /**
     * The constructor.
     *
     * @param state              entry state object
     * @param strategy           scheduling strategy
     * @param storage            storage to save entry in
     * @param action             the action that should be executed when this entry fires
     * @param isSavedRemotely    if the entry is saved in remote storage (as it was restored from it)
     * @throws ResolutionException if fails to resolve any dependencies
     * @throws ReadValueException if error occurs reading values from state object
     * @throws InvalidArgumentException if error occurs reading values from state object
     */
    EntryImpl(
            final IObject state,
            final ISchedulingStrategy strategy,
            final ISchedulerEntryStorage storage,
            final ISchedulerAction action,
            final boolean isSavedRemotely)
                throws ResolutionException, ReadValueException, InvalidArgumentException {
        IFieldName idFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "entryId");

        this.storage = storage;
        this.state = state;
        this.strategy = strategy;
        this.action = action;
        this.isSavedRemotely = isSavedRemotely;
        this.lastScheduledTime = Long.MAX_VALUE;

        this.timerTask = new AtomicReference<>(null);
        this.isCancelled = false;

        this.id = (String) state.getValue(idFieldName);
    }

    /**
     * Create a new entry from given description.
     *
     * @param args       entry description
     * @param storage    the {@link EntryStorage storage} to store entry in (if necessary)
     *
     * @return the new entry
     *
     * @throws ResolutionException if cannot resolve dependencies
     * @throws ReadValueException if cannot read description fields
     * @throws ChangeValueException if can not modify description to use it as entry state object
     * @throws InvalidArgumentException if invalid arguments were passed to some method
     * @throws SchedulingStrategyExecutionException if error occurs executing scheduling strategy
     * @throws SchedulerActionInitializationException if error occurs initializing scheduler action associated with the entry
     */
    public static EntryImpl newEntry(final IObject args, final ISchedulerEntryStorage storage)
            throws ResolutionException, ReadValueException, ChangeValueException, InvalidArgumentException,
            SchedulingStrategyExecutionException, SchedulerActionInitializationException {
        IFieldName idFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "entryId");
        IFieldName actionFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "action");
        IFieldName strategyFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "strategy");
        IFieldName schedulingFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "scheduling");

        IObject schedulingArguments = (IObject) args.getValue(schedulingFieldName);
        IObject state = args;

        if (null == schedulingArguments) {
            schedulingArguments = args;
        } else {
            // If arguments contain "scheduling" field then object contained in that field will be used to initialize scheduling strategy
            // and the new IObject will be created for entry state. The object contained in "scheduling" field will be read-only.
            state = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()));
        }

        // Resolve strategy and save it's key to entry state
        String strategyKeyName = (String) schedulingArguments.getValue(strategyFieldName);
        ISchedulingStrategy strategy = IOC.resolve(Keys.getOrAdd(strategyKeyName));
        state.setValue(strategyFieldName, strategyKeyName);

        // Generate entry id
        String id = UUID.randomUUID().toString();
        state.setValue(idFieldName, id);

        // Resolve action and save it's key to entry state
        Object actionKeyName = args.getValue(actionFieldName);

        if (null == actionKeyName) {
            actionKeyName = DEFAULT_ACTION_KEY_NAME;
        }

        ISchedulerAction action = IOC.resolve(Keys.getOrAdd(actionKeyName.toString()));

        state.setValue(actionFieldName, actionKeyName);

        // Create the entry
        EntryImpl entry = new EntryImpl(
                state,
                strategy,
                storage,
                action,
                false);

        // Init action and then scheduling strategy. If action cannot be initialized the scheduling strategy will not be initialized and the
        // entry will not be scheduled
        action.init(entry, args);
        strategy.init(entry, schedulingArguments);

        return entry;
    }

    /**
     * Create new entry from entry state object loaded from database.
     *
     * @param savedState    the saved entry state object
     * @param storage       the storage to use to store the entry (or to delete it from when it is completed)
     *
     * @return the new entry
     *
     * @throws ResolutionException if cannot resolve dependencies
     * @throws ReadValueException if cannot read description fields
     * @throws InvalidArgumentException if invalid arguments were passed to some method
     * @throws SchedulingStrategyExecutionException if error occurs executing scheduling strategy
     */
    public static EntryImpl restoreEntry(final IObject savedState, final ISchedulerEntryStorage storage)
            throws ResolutionException, ReadValueException, InvalidArgumentException,
            SchedulingStrategyExecutionException {
        IFieldName strategyFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "strategy");
        IFieldName actionFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "action");

        ISchedulingStrategy strategy = IOC.resolve(Keys.getOrAdd((String) savedState.getValue(strategyFieldName)));

        Object actionKey = savedState.getValue(actionFieldName);

        if (null == actionKey) {
            actionKey = DEFAULT_ACTION_KEY_NAME;
        }

        ISchedulerAction action = IOC.resolve(Keys.getOrAdd(actionKey.toString()));

        EntryImpl entry = new EntryImpl(
                savedState,
                strategy,
                storage,
                action,
                true);

        strategy.restore(entry);
        return entry;
    }

    private void executeTask() throws TaskExecutionException {
        try {
            action.execute(this);
            strategy.postProcess(this);
        } catch (SchedulerActionExecutionException | SchedulingStrategyExecutionException e) {
            try {
                strategy.processException(this, e);
            } catch (SchedulingStrategyExecutionException ee) {
                ee.addSuppressed(e);
                throw new TaskExecutionException(ee);
            }
        }
    }

    @Override
    public IObject getState() {
        return state;
    }

    @Override
    public void save() throws EntryStorageAccessException {
        storage.save(this);
        this.isSavedRemotely = true;
    }

    @Override
    public void cancel() throws EntryStorageAccessException {
        if (!isCancelled) {
            this.isCancelled = true;
            ITimerTask tt = timerTask.getAndSet(null);

            if (null != tt) {
                tt.cancel();
            }

            storage.delete(this);
        }
    }

    @Override
    public void scheduleNext(final long time)
            throws EntryScheduleException {
        try {
            ITimerTask tt = timerTask.get();

            if (null == tt) {
                if (isCancelled) {
                    // This entry is a "zombie"
                    storage.delete(this);
                    return;
                }

                tt = this.timerTask.getAndSet(storage.getTimer().schedule(this.task, time));

                if (null != tt) {
                    tt.cancel();
                }
            } else {
                tt.reschedule(time);
            }

            lastScheduledTime = time;

            // Will create "zombie" if this entry was cancelled after timerTask.get() call
            storage.notifyActive(this);
        } catch (TaskScheduleException | EntryStorageAccessException e) {
            throw new EntryScheduleException("Could not reschedule entry.", e);
        }
    }

    @Override
    public long getLastTime() {
        return lastScheduledTime;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void suspend() throws EntryStorageAccessException {
        ITimerTask tt = timerTask.getAndSet(null);

        if (null != tt) {
            tt.cancel();
            storage.notifyInactive(this, !isSavedRemotely);
        }
    }

    @Override
    public void awake() throws EntryStorageAccessException, EntryScheduleException {
        long lastTime = getLastTime();
        if (null == timerTask.get() && lastTime != Long.MAX_VALUE) {
            scheduleNext(lastTime);
        }
    }

    @Override
    public boolean isAwake() {
        return !isCancelled && timerTask.get() != null;
    }
}
