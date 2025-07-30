package info.smart_tools.smartactors.task.interfaces.itask;

import info.smart_tools.smartactors.task.interfaces.itask.exception.TaskExecutionException;

/**
 * Task is a unit of work that may be executed.
 */
public interface ITask {
    /**
     * Execute the task.
     *
     * @throws TaskExecutionException if error occurs in process of task execution
     */
    void execute() throws TaskExecutionException;
}
