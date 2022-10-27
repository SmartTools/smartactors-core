package info.smart_tools.smartactors.task.thread_pool;

import info.smart_tools.smartactors.class_management.interfaces.imodule.IModule;
import info.smart_tools.smartactors.class_management.module_manager.ModuleManager;
import info.smart_tools.smartactors.scope.iscope_provider_container.exception.ScopeProviderException;
import info.smart_tools.smartactors.scope.scope_provider.ScopeProvider;
import info.smart_tools.smartactors.task.interfaces.itask.ITask;
import info.smart_tools.smartactors.task.interfaces.itask.exception.TaskExecutionException;

import java.util.concurrent.atomic.AtomicReference;

/**
 * The thread waiting for a task and returning itself to the {@link ThreadPool} when done.
 */
class ThreadImpl implements Runnable {
    private final Thread thread;
    private final ThreadPool pool;
    private final AtomicReference<ITask> setTaskRef;
    private final Object lock;

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            ModuleManager.setCurrentModule(pool.getModule());
            try {
                ScopeProvider.setCurrentScope(pool.getScope());
            } catch (ScopeProviderException e) {
                e.printStackTrace();
            }
            try {
                synchronized (lock) {
                    while (setTaskRef.get() == null) {
                        lock.wait();
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                continue;
            }

            try {
                setTaskRef.get().execute();
            } catch (Throwable e) { // was TaskExecutionException before
                                    // changed to catch runtime exceptions to prevent thread loss
                IModule module = ModuleManager.getCurrentModule();
                if (module != null) {
                    System.out.println("[FAIL] Exception thrown in context of module " +
                            module.getName() + ":" + module.getVersion());
                }
                e.printStackTrace();
                // ToDo: for a while we did not have situations when we can check that
                // ToDo: re-creation of thread may help in such a case of thrown Error
                    /*if (!(e instanceof Exception)) {
                        System.out.println("[FAIL] Thread " + thread.getName() + " have got Error exception " +
                                "and will be killed.\n New thread with same name is created.");
                        pool.returnThread(new ThreadImpl(pool, thread.getName()));
                        Thread.currentThread().interrupt();
                        return;
                    }*/
            }

            setTaskRef.set(null);
            pool.returnThread(this);
        }
    }

    /**
     * The constructor.
     *
     * @param pool          the thread pool that owns this thread
     * @param threadName    the name for thread to create
     */
    ThreadImpl(final ThreadPool pool, final String threadName) {
        this.pool = pool;

        this.setTaskRef = new AtomicReference<>(null);
        this.lock = new Object();

        this.thread = new Thread(this, threadName);

        this.thread.start();
    }

    /**
     * Start execution of given task in this thread.
     *
     * @param task the task to execute.
     * @throws TaskExecutionException if another task is being executed on this thread
     * @throws TaskExecutionException if the thread is already not alive
     */
    void execute(final ITask task) throws TaskExecutionException {
        if (!setTaskRef.compareAndSet(null, task)) {
            throw new TaskExecutionException("Another task is being executed.");
        }

        if (!thread.isAlive() || thread.isInterrupted()) {
            throw new TaskExecutionException("Thread is dead.");
        }

        synchronized (this.lock) {
            this.lock.notifyAll();
        }
    }

    /**
     * Interrupt the underlying Java thread (using {@link Thread#interrupt()} method).
     */
    void interrupt() {
        this.thread.interrupt();
    }
}
