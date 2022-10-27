package info.smart_tools.smartactors.task.imanaged_task;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.task.interfaces.itask.ITask;

/**
 * Interface for a task that has some additional objects associated with it that may be used to notify the task on some events/get or change
 * task state, etc.
 */
public interface IManagedTask extends ITask {
    /**
     * Get an object of given class associated with this task.
     *
     * @param clazz    class of the required object
     * @param <T>      type of the required object
     * @return the object associated with the task
     * @throws InvalidArgumentException if the task does't have associated object of given class
     */
    <T> T getAs(Class<T> clazz) throws InvalidArgumentException;
}
