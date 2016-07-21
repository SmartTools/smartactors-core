package info.smart_tools.smartactors.core.ithread_pool;

import info.smart_tools.smartactors.core.itask.ITask;
import info.smart_tools.smartactors.core.itask.exception.TaskExecutionException;

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
}
