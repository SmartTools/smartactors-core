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
import info.smart_tools.smartactors.task.interfaces.itask_dispatcher.ITaskDispatcher;
import info.smart_tools.smartactors.task.interfaces.ithread_pool.IThreadPool;
import info.smart_tools.smartactors.task.task_dispatcher.TaskDispatcher;
import info.smart_tools.smartactors.task.thread_pool.ThreadPool;

/**
 * Plugin that registers strategies which creating new instances of thread pool and task dispatcher in IOC.
 */
public class TaskDispatcherAndThreadPoolPlugin implements IPlugin {

    private final IBootstrap<IBootstrapItem<String>> bootstrap;

    /**
     * The constructor.
     *
     * @param bootstrap    the bootstrap
     */
    public TaskDispatcherAndThreadPoolPlugin(final IBootstrap<IBootstrapItem<String>> bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void load() throws PluginException {
        try {
            IBootstrapItem<String> item = new BootstrapItem("threadPoolAndTaskDispatcher");
            item
                    .after("IOC")
                    .after("IFieldNamePlugin")
                    .before("starter")
                    .process(() -> {
                        try {
                            IOC.register(
                                    Keys.getKeyByName(ITaskDispatcher.class.getCanonicalName()),
                                    new ApplyFunctionToArgumentsStrategy(args -> {
                                            try {
                                                return new TaskDispatcher(
                                                        (IQueue) args[0],
                                                        (IThreadPool) args[1],
                                                        (int) args[2],
                                                        (int) args[3]
                                                );
                                            } catch (Exception e) {
                                                throw new RuntimeException(e);
                                            }
                            }));
                            IOC.register(
                                    Keys.getKeyByName(IThreadPool.class.getCanonicalName()),
                                    new ApplyFunctionToArgumentsStrategy(args -> {
                                        try {
                                            return new ThreadPool((int) args[0]);
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
        } catch (Exception e) {
            throw new PluginException(e);
        }
    }
}
