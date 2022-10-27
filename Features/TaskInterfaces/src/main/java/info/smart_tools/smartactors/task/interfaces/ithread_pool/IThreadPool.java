package info.smart_tools.smartactors.task.interfaces.ithread_pool;

import info.smart_tools.smartactors.task.interfaces.itask.ITask;
import info.smart_tools.smartactors.task.interfaces.itask.exception.TaskExecutionException;

/**
 * Thread pool.
 */
public interface IThreadPool {
    /**
     * Try to execute given task on any free thread.
     *
     * @param task the task to execute
     * @return {@code true} if there was a free thread and execution of task started on it or {@code false} if there was
     *                      no such thread.
     * @throws TaskExecutionException if any error occurs
     */
    boolean tryExecute(ITask task) throws TaskExecutionException;

    /**
     * Terminate the thread pool.
     *
     * After completion of call to {@code #terminate()} no one call to {@link #tryExecute(ITask)} will finish successfully and all threads
     * of pool will be destroyed as soon as possible (immediately or after completion of current task).
     */
    void terminate();
}
