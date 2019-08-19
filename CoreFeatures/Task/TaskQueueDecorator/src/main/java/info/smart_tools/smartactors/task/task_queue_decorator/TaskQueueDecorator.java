package info.smart_tools.smartactors.task.task_queue_decorator;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.class_management.interfaces.imodule.IModule;
import info.smart_tools.smartactors.class_management.module_manager.ModuleManager;
import info.smart_tools.smartactors.scope.iscope.IScope;
import info.smart_tools.smartactors.scope.iscope_provider_container.exception.ScopeProviderException;
import info.smart_tools.smartactors.scope.scope_provider.ScopeProvider;
import info.smart_tools.smartactors.task.interfaces.iqueue.IQueue;
import info.smart_tools.smartactors.task.interfaces.itask.ITask;


/**
 * Decorator for implementation of {@link IQueue}.
 */
public class TaskQueueDecorator implements IQueue<ITask> {

    private final IQueue<ITask> queue;

    /**
     * The constructor.
     *
     * @param queue    underlying standard queue
     * @throws InvalidArgumentException if queue queue is {@code null}
     */
    public TaskQueueDecorator(final IQueue<ITask> queue) {
        this.queue = queue;
    }

    @Override
    public void put(final ITask item) throws InterruptedException {
        IModule module = ModuleManager.getCurrentModule();
        IScope scope;
        try {
            scope = ScopeProvider.getCurrentScope();
        } catch (ScopeProviderException e) {
            throw new InterruptedException(e.getMessage());
        }
        this.queue.put(() -> {
            ModuleManager.setCurrentModule(module);
            try {
                ScopeProvider.setCurrentScope(scope);
            } catch (ScopeProviderException e) {
                throw new RuntimeException(e);
            }
            item.execute();
        });
    }

    @Override
    public ITask take() throws InterruptedException {
        return queue.take();
    }

    @Override
    public ITask tryTake() {
        return queue.tryTake();
    }

    @Override
    public void addNewItemCallback(final Runnable callback) {
        queue.addNewItemCallback(callback);
    }

    @Override
    public void removeNewItemCallback(final Runnable callback) {
        queue.removeNewItemCallback(callback);
    }
}
