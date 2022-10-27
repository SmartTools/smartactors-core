package info.smart_tools.smartactors.task.thread_pool;

import info.smart_tools.smartactors.class_management.interfaces.imodule.IModule;
import info.smart_tools.smartactors.class_management.module_manager.ModuleManager;
import info.smart_tools.smartactors.scope.iscope.IScope;
import info.smart_tools.smartactors.scope.iscope_provider_container.exception.ScopeProviderException;
import info.smart_tools.smartactors.scope.scope_provider.ScopeProvider;
import info.smart_tools.smartactors.task.interfaces.itask.ITask;
import info.smart_tools.smartactors.task.interfaces.itask.exception.TaskExecutionException;
import info.smart_tools.smartactors.task.interfaces.ithread_pool.IThreadPool;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Implementation of {@link IThreadPool}.
 */
public class ThreadPool implements IThreadPool {
    private final Queue<ThreadImpl> threadsQueue;
    private IScope scope;
    private IModule module;
    private boolean terminating = false;

    /**
     * The constructor.
     *
     * @param threadCount    initial count of threads.
     */
    public ThreadPool(final int threadCount) {

        this.module = ModuleManager.getCurrentModule();
        try {
            this.scope = ScopeProvider.getCurrentScope();
        } catch (ScopeProviderException e) {
            this.scope = null;
        }

        threadsQueue = new ConcurrentLinkedQueue<>();

        for (int i = 0; i < threadCount; i++) {
            threadsQueue.offer(new ThreadImpl(this, "TaskThread"+(i+1)));
        }
    }

    @Override
    public boolean tryExecute(final ITask task)
            throws TaskExecutionException {
        ThreadImpl thread = threadsQueue.poll();

        if (null != thread) {
            thread.execute(task);
            return true;
        }

        return false;
    }

    @Override
    public void terminate() {
        terminating = true;

        ThreadImpl thread;

        while (null != (thread = threadsQueue.poll())) {
            thread.interrupt();
        }
    }

    /**
     * Returns the thread to this pool.
     *
     * @param thread the thread
     */
    void returnThread(final ThreadImpl thread) {
        if (terminating || !threadsQueue.offer(thread)) {
            thread.interrupt();
        }
    }

    IScope getScope() {
        return this.scope;
    }

    IModule getModule() {
        return this.module;
    }
}
