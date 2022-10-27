package info.smart_tools.smartactors.shutdown.shutdown_task_processing_strategies;

import info.smart_tools.smartactors.shutdown.ishutdown_aware_task.IShutdownAwareTask;
import info.smart_tools.smartactors.shutdown.ishutdown_aware_task.exceptions.ShutdownAwareTaskNotificationException;
import info.smart_tools.smartactors.task.itask_execution_state.ITaskExecutionState;
import info.smart_tools.smartactors.task.itask_preprocess_strategy.exceptions.TaskProcessException;
import org.junit.Test;

import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

/**
 * Test for {@link NotifyTaskStrategy}.
 */
public class NotifyTaskStrategyTest {
    @Test
    public void Should_notifyAndExecuteTask()
            throws Exception {
        ITaskExecutionState taskExecutionState = mock(ITaskExecutionState.class);
        IShutdownAwareTask shutdownAwareTask = mock(IShutdownAwareTask.class);

        when(taskExecutionState.getTaskAs(same(IShutdownAwareTask.class))).thenReturn(shutdownAwareTask);

        new NotifyTaskStrategy().process(taskExecutionState);

        verify(shutdownAwareTask).notifyShuttingDown();
        verify(taskExecutionState).execute();
    }

    @Test(expected = TaskProcessException.class)
    public void Should_wrapNotificationException()
            throws Exception {
        ITaskExecutionState taskExecutionState = mock(ITaskExecutionState.class);
        IShutdownAwareTask shutdownAwareTask = mock(IShutdownAwareTask.class);

        when(taskExecutionState.getTaskAs(same(IShutdownAwareTask.class))).thenReturn(shutdownAwareTask);
        doThrow(ShutdownAwareTaskNotificationException.class).when(shutdownAwareTask).notifyShuttingDown();

        new NotifyTaskStrategy().process(taskExecutionState);
    }
}