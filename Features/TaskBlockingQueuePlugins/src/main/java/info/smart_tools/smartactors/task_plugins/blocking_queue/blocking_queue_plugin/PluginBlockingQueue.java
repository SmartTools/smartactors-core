package info.smart_tools.smartactors.task_plugins.blocking_queue.blocking_queue_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.task.blocking_queue.BlockingQueue;
import info.smart_tools.smartactors.task.interfaces.iqueue.IQueue;
import info.smart_tools.smartactors.task.task_queue_decorator.TaskQueueDecorator;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * Plugin that registers a blocking queue creation strategy in IOC.
 */
public class PluginBlockingQueue implements IPlugin {
    private static final int DEFAULT_QUEUE_SIZE = 16;

    private final IBootstrap<IBootstrapItem<String>> bootstrap;

    /**
     * The constructor.
     *
     * @param bootstrap    the bootstrap
     */
    public PluginBlockingQueue(final IBootstrap<IBootstrapItem<String>> bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    @SuppressWarnings("unchecked")
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
                                    IFieldName queueSizeFieldName = IOC.resolve(
                                            Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "queueSize");
                                    int queueSize = DEFAULT_QUEUE_SIZE;

                                    if (args.length > 0) {
                                        IObject conf = (IObject) args[0];
                                        queueSize = (int) conf.getValue(queueSizeFieldName);
                                    }

                                    return new TaskQueueDecorator(new BlockingQueue<>(new ArrayBlockingQueue<>(queueSize)));
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            }));
                        } catch (ResolutionException | RegistrationException | InvalidArgumentException e) {
                            throw new ActionExecutionException(e);
                        }
                    });

            bootstrap.add(item);
        } catch (Exception e) {
            throw new PluginException(e);
        }
    }
}
