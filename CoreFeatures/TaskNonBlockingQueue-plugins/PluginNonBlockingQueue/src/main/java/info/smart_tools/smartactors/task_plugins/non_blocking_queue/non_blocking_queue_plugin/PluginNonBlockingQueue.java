package info.smart_tools.smartactors.task_plugins.non_blocking_queue.non_blocking_queue_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.ioc.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.task.interfaces.iqueue.IQueue;
import info.smart_tools.smartactors.task.non_blocking_queue.NonBlockingQueue;
import info.smart_tools.smartactors.task.task_queue_decorator.TaskQueueDecorator;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Plugin that registers non-blocking queue in IOC.
 */
public class PluginNonBlockingQueue implements IPlugin {

    private final IBootstrap<IBootstrapItem<String>> bootstrap;

    /**
     * The constructor.
     *
     * @param bootstrap    the bootstrap
     */
    public PluginNonBlockingQueue(final IBootstrap<IBootstrapItem<String>> bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    public void load() throws PluginException {
        try {
            IBootstrapItem<String> item = new BootstrapItem("queue");

            item
                    .after("IOC")
                    .after("IFieldNamePlugin")
                    .process(() -> {
                        try {
                            IOC.register(Keys.getKeyByName(IQueue.class.getCanonicalName()), new ApplyFunctionToArgumentsStrategy(args -> {
                                try {
                                    return new TaskQueueDecorator(new NonBlockingQueue<>(new ConcurrentLinkedQueue<>()));
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            }));
                        } catch (ResolutionException | RegistrationException | InvalidArgumentException e) {
                            throw new ActionExecutionException(e);
                        }
                    })
                    .revertProcess(() -> {
                        String[] itemNames = { IQueue.class.getCanonicalName() };
                        Keys.unregisterByNames(itemNames);
                    });

            bootstrap.add(item);
        } catch (InvalidArgumentException e) {
            throw new PluginException(e);
        }
    }
}
