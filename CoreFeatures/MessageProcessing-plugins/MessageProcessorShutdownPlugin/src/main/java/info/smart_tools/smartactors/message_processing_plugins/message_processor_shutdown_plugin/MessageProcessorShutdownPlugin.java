package info.smart_tools.smartactors.message_processing_plugins.message_processor_shutdown_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.i_addition_dependency_strategy.IAdditionDependencyStrategy;
import info.smart_tools.smartactors.base.interfaces.i_addition_dependency_strategy.exception.AdditionDependencyStrategyException;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import info.smart_tools.smartactors.message_processing.message_processor.MessageProcessor;
import info.smart_tools.smartactors.task.itask_preprocess_strategy.ITaskProcessStrategy;

public class MessageProcessorShutdownPlugin extends BootstrapPlugin {

    /**
     * The constructor.
     *
     * @param bootstrap    the bootstrap
     */
    public MessageProcessorShutdownPlugin(final IBootstrap bootstrap) {
            super(bootstrap);
    }

    @Item("message_processor_shutdown_strategy")
    @After({
            "shutdown_task_process_strategies",
    })
    public void registerMessageProcessorShutdownStrategies()
            throws ResolutionException, AdditionDependencyStrategyException, InvalidArgumentException {
        IAdditionDependencyStrategy strategy = IOC.resolve(Keys.getOrAdd(
                "expandable_strategy#shutdown mode task processing strategy by task class"));
        ITaskProcessStrategy taskProcessStrategy = IOC.resolve(Keys.getOrAdd("notify task processing strategy"));
        strategy.register(MessageProcessor.class, new SingletonStrategy(taskProcessStrategy));
    }
}
