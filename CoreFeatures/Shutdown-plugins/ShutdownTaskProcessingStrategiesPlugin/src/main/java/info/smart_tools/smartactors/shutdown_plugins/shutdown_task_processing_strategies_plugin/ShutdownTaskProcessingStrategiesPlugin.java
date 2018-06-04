package info.smart_tools.smartactors.shutdown_plugins.shutdown_task_processing_strategies_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IBiFunction;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecuteException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.FunctionExecutionException;
import info.smart_tools.smartactors.base.interfaces.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.base.iup_counter.IUpCounter;
import info.smart_tools.smartactors.base.iup_counter.exception.UpCounterCallbackExecutionException;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.base.strategy.strategy_storage_with_cache_strategy.StrategyStorageWithCacheStrategy;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.DeletionException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
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
        IOC.register(Keys.getOrAdd("ignore task processing strategy"),
                new SingletonStrategy(new IgnoreTaskStrategy()));
        IOC.register(Keys.getOrAdd("execute task processing strategy"),
                new SingletonStrategy(new ExecuteTaskStrategy()));
        IOC.register(Keys.getOrAdd("notify task processing strategy"),
                new SingletonStrategy(new NotifyTaskStrategy()));
        IOC.register(Keys.getOrAdd("limit trials task processing strategy"),
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

        IResolveDependencyStrategy strategyStorage = new StrategyStorageWithCacheStrategy(
                a -> a,
                (IBiFunction<Map<Class, Object>, Class, Object>) (map, clz) -> {
                    for (Map.Entry<Class, Object> entry : map.entrySet()) {
                        if (clz.isAssignableFrom(entry.getKey())) {
                            return entry.getValue();
                        }
                    }

                    return null;
                }
        );

        IOC.register(Keys.getOrAdd("shutdown mode task processing strategy by task class"),
                strategyStorage);
        IOC.register(Keys.getOrAdd("expandable_strategy#shutdown mode task processing strategy by task class"),
                new SingletonStrategy(strategyStorage));

        IOC.register(Keys.getOrAdd("task processing strategy for shutdown mode"),
                new SingletonStrategy(new CompositeStrategy(
                        Keys.getOrAdd("shutdown mode task processing strategy by task class"),
                        IOC.resolve(Keys.getOrAdd("execute task processing strategy")))));
    }

    @ItemRevert("shutdown_task_process_strategies")
    public void deregisterStrategies() {
        String itemName = "shutdown_task_process_strategies";
        String keyName = "";

        try {
            keyName = "task processing strategy for shutdown mode";
            IOC.remove(Keys.getOrAdd(keyName));
        } catch(DeletionException e) {
            System.out.println("[WARNING] Deregitration of \""+keyName+"\" has failed while reverting \""+itemName+"\" plugin.");
        } catch (ResolutionException e) { }

        try {
            keyName = "expandable_strategy#shutdown mode task processing strategy by task class";
            IOC.remove(Keys.getOrAdd(keyName));
        } catch(DeletionException e) {
            System.out.println("[WARNING] Deregitration of \""+keyName+"\" has failed while reverting \""+itemName+"\" plugin.");
        } catch (ResolutionException e) { }

        try {
            keyName = "shutdown mode task processing strategy by task class";
            IOC.remove(Keys.getOrAdd(keyName));
        } catch(DeletionException e) {
            System.out.println("[WARNING] Deregitration of \""+keyName+"\" has failed while reverting \""+itemName+"\" plugin.");
        } catch (ResolutionException e) { }

        try {
            keyName = "limit trials task processing strategy";
            IOC.remove(Keys.getOrAdd(keyName));
        } catch(DeletionException e) {
            System.out.println("[WARNING] Deregitration of \""+keyName+"\" has failed while reverting \""+itemName+"\" plugin.");
        } catch (ResolutionException e) { }

        try {
            keyName = "notify task processing strategy";
            IOC.remove(Keys.getOrAdd(keyName));
        } catch(DeletionException e) {
            System.out.println("[WARNING] Deregitration of \""+keyName+"\" has failed while reverting \""+itemName+"\" plugin.");
        } catch (ResolutionException e) { }

        try {
            keyName = "execute task processing strategy";
            IOC.remove(Keys.getOrAdd(keyName));
        } catch(DeletionException e) {
            System.out.println("[WARNING] Deregitration of \""+keyName+"\" has failed while reverting \""+itemName+"\" plugin.");
        } catch (ResolutionException e) { }

        try {
            keyName = "ignore task processing strategy";
            IOC.remove(Keys.getOrAdd(keyName));
        } catch(DeletionException e) {
            System.out.println("[WARNING] Deregitration of \""+keyName+"\" has failed while reverting \""+itemName+"\" plugin.");
        } catch (ResolutionException e) { }
    }

    @Item("task_dispatcher_pre_shutdown_mode_callbacks")
    @After({
        "shutdown_task_process_strategies",
        "root_upcounter",
    })
    public void registerUpcounterCallbackForTaskDispatcherShutdown()
            throws ResolutionException, UpCounterCallbackExecutionException {
        IUpCounter upCounter = IOC.resolve(Keys.getOrAdd("root upcounter"));

        upCounter.onShutdownRequest(mode -> {
            try {
                ITaskDispatcher taskDispatcher = IOC.resolve(Keys.getOrAdd("task_dispatcher"));
                ITaskProcessStrategy taskProcessStrategy = IOC.resolve(Keys.getOrAdd("task processing strategy for shutdown mode"));
                taskDispatcher.setProcessStrategy(taskProcessStrategy);
            } catch (ResolutionException e) {
                throw new ActionExecuteException(e);
            }
        });
    }

    @ItemRevert("task_dispatcher_pre_shutdown_mode_callbacks")
    public void deregisterUpcounterCallbackForTaskDispatcherShutdown() {
    }
}
