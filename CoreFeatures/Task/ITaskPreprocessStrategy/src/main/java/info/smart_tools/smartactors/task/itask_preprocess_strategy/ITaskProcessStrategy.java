package info.smart_tools.smartactors.task.itask_preprocess_strategy;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.task.interfaces.itask.exception.TaskExecutionException;
import info.smart_tools.smartactors.task.itask_execution_state.ITaskExecutionState;
import info.smart_tools.smartactors.task.itask_preprocess_strategy.exceptions.TaskProcessException;

/**
 * Strategy that is called by task dispatcher to decide if a particular task should be executed and what additional actions should be taken
 * before or  after it's execution.
 */
public interface ITaskProcessStrategy {
    /**
     *
     * @param state
     * @throws TaskExecutionException if error occurs executing the task
     * @throws InvalidArgumentException if the task being executed is not acceptable for this strategy
     * @throws TaskProcessException if any other error occurs
     */
    void process(ITaskExecutionState state) throws TaskExecutionException, InvalidArgumentException, TaskProcessException;
}
