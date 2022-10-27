package info.smart_tools.smartactors.shutdown_plugins.shutdown_task_processing_strategies_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IFunctionTwoArgs;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.FunctionExecutionException;
import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.iup_counter.IUpCounter;
import info.smart_tools.smartactors.base.iup_counter.exception.UpCounterCallbackExecutionException;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.base.strategy.strategy_storage_with_cache_strategy.StrategyStorageWithCacheStrategy;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.ioc.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.shutdown.shutdown_task_processing_strategies.*;
import info.smart_tools.smartactors.task.interfaces.itask_dispatcher.ITaskDispatcher;
import info.smart_tools.smartactors.task.itask_preprocess_strategy.ITaskProcessStrategy;

import java.util.Map;

public class ShutdownTaskProcessingStrategiesPlugin extends BootstrapPlugin {
    /**
     * The constructor.
     *
     * @param bootstrap    the bootstrap
     */
    public ShutdownTaskProcessingStrategiesPlugin(final IBootstrap bootstrap) {
            super(bootstrap);
    }

    @Item("shutdown_task_process_strategies")
    @After({"IOC"})
    public void registerStrategies()
            throws ResolutionException, InvalidArgumentException, RegistrationException {
        IOC.register(Keys.getKeyByName("ignore task processing strategy"),
                new SingletonStrategy(new IgnoreTaskStrategy()));
        IOC.register(Keys.getKeyByName("execute task processing strategy"),
                new SingletonStrategy(new ExecuteTaskStrategy()));
        IOC.register(Keys.getKeyByName("notify task processing strategy"),
                new SingletonStrategy(new NotifyTaskStrategy()));
        IOC.register(Keys.getKeyByName("limit trials task processing strategy"),
                new ApplyFunctionToArgumentsStrategy(args -> {
                    int maxTrials = (int) args[0];
                    int silentTrials = maxTrials;

                    if (args.length > 1) {
                        silentTrials = (int) args[1];
                    }

                    try {
                        return new LimitTrialCountStrategy(maxTrials, silentTrials);
                    } catch (InvalidArgumentException e) {
                        throw new FunctionExecutionException(e);
                    }
                }));

        IStrategy strategyStorage = new StrategyStorageWithCacheStrategy(
                a -> a,
                (IFunctionTwoArgs<Map<Class, Object>, Class, Object>) (map, clz) -> {
                    for (Map.Entry<Class, Object> entry : map.entrySet()) {
                        if (clz.isAssignableFrom(entry.getKey())) {
                            return entry.getValue();
                        }
                    }

                    return null;
                }
        );

        IOC.register(Keys.getKeyByName("shutdown mode task processing strategy by task class"),
                strategyStorage);
        IOC.register(Keys.getKeyByName("expandable_strategy#shutdown mode task processing strategy by task class"),
                new SingletonStrategy(strategyStorage));

        IOC.register(Keys.getKeyByName("task processing strategy for shutdown mode"),
                new SingletonStrategy(new CompositeStrategy(
                        Keys.getKeyByName("shutdown mode task processing strategy by task class"),
                        IOC.resolve(Keys.getKeyByName("execute task processing strategy")))));
    }

    @ItemRevert("shutdown_task_process_strategies")
    public void unregisterStrategies() {
        String[] itemNames = {
                "task processing strategy for shutdown mode",
                "expandable_strategy#shutdown mode task processing strategy by task class",
                "shutdown mode task processing strategy by task class",
                "limit trials task processing strategy",
                "notify task processing strategy",
                "execute task processing strategy",
                "ignore task processing strategy"
        };
        Keys.unregisterByNames(itemNames);
    }

    @Item("task_dispatcher_pre_shutdown_mode_callbacks")
    @After({
        "shutdown_task_process_strategies",
        "root_upcounter",
    })
    @Before({"read_initial_config"})
    public void registerUpcounterCallbackForTaskDispatcherShutdown()
            throws ResolutionException, UpCounterCallbackExecutionException {
        IUpCounter upCounter = IOC.resolve(Keys.getKeyByName("root upcounter"));

        upCounter.onShutdownRequest(this.toString(), mode -> {
            try {
                ITaskDispatcher taskDispatcher = IOC.resolve(Keys.getKeyByName("task_dispatcher"));
                ITaskProcessStrategy taskProcessStrategy = IOC.resolve(Keys.getKeyByName("task processing strategy for shutdown mode"));
                taskDispatcher.setProcessStrategy(taskProcessStrategy);
            } catch (ResolutionException e) {
                throw new ActionExecutionException(e);
            }
        });
    }

    @ItemRevert("task_dispatcher_pre_shutdown_mode_callbacks")
    public void unregisterUpcounterCallbackForTaskDispatcherShutdown() {
    }
}
