package info.smart_tools.smartactors.shutdown.shutdown_task_processing_strategies;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.shutdown.ishutdown_aware_task.IShutdownAwareTask;
import info.smart_tools.smartactors.shutdown.ishutdown_aware_task.exceptions.ShutdownAwareTaskNotificationException;
import info.smart_tools.smartactors.task.interfaces.itask.exception.TaskExecutionException;
import info.smart_tools.smartactors.task.itask_execution_state.ITaskExecutionState;
import info.smart_tools.smartactors.task.itask_preprocess_strategy.ITaskProcessStrategy;
import info.smart_tools.smartactors.task.itask_preprocess_strategy.exceptions.TaskProcessException;

/**
 * {@link ITaskProcessStrategy Task process strategy} that notifies task implementing {@link IShutdownAwareTask special interface}.
 */
public class NotifyTaskStrategy implements ITaskProcessStrategy {
    @Override
    public void process(final ITaskExecutionState state)
            throws TaskExecutionException, InvalidArgumentException, TaskProcessException {
        try {
            state.getTaskAs(IShutdownAwareTask.class).notifyShuttingDown();
        } catch (ShutdownAwareTaskNotificationException e) {
            throw new TaskProcessException(e);
        }

        state.execute();
    }
}
