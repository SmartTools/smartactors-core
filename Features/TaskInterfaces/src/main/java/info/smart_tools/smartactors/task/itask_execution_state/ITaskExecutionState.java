package info.smart_tools.smartactors.task.itask_execution_state;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.task.interfaces.itask.ITask;
import info.smart_tools.smartactors.task.interfaces.itask.exception.TaskExecutionException;

public interface ITaskExecutionState {
    /**
     * Execute the task.
     *
     * @throws TaskExecutionException if error occurs executing the task
     */
    void execute() throws TaskExecutionException;

    /**
     * Get an object of given class that is associated with the task.
     *
     * @param clazz    object class
     * @param <T>      object type
     * @return object associated with the task
     * @throws InvalidArgumentException if there is no object of given class is associated with the task
     */
    <T> T getTaskAs(Class<T> clazz) throws InvalidArgumentException;

    /**
     * @return class of the task
     */
    Class<? extends ITask> getTaskClass();
}
