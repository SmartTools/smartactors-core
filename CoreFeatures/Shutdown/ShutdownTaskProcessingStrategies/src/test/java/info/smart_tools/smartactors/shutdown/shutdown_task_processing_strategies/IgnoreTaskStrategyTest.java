package info.smart_tools.smartactors.shutdown.shutdown_task_processing_strategies;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.shutdown.ishutdown_aware_task.IShutdownAwareTask;
import info.smart_tools.smartactors.shutdown.ishutdown_aware_task.exceptions.ShutdownAwareTaskNotificationException;
import info.smart_tools.smartactors.task.itask_execution_state.ITaskExecutionState;
import info.smart_tools.smartactors.task.itask_preprocess_strategy.exceptions.TaskProcessException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

/**
 * Test for {@link IgnoreTaskStrategy}.
 */
public class IgnoreTaskStrategyTest {
    private IShutdownAwareTask shutdownAwareTask;
    private ITaskExecutionState taskExecutionState;

    @Before
    public void setUp()
            throws Exception {
        shutdownAwareTask = mock(IShutdownAwareTask.class);
        taskExecutionState = mock(ITaskExecutionState.class);
    }

    @Test
    public void Should_doNothingWithTaskThatIsNotAwareOfShutdownRequests()
            throws Exception {
        when(taskExecutionState.getTaskAs(same(IShutdownAwareTask.class)))
                .thenThrow(InvalidArgumentException.class);

        new IgnoreTaskStrategy().process(taskExecutionState);

        verify(taskExecutionState).getTaskAs(same(IShutdownAwareTask.class));
        verifyNoMoreInteractions(taskExecutionState);
    }

    @Test
    public void Should_notifyTheTskThatItIsIgnored()
            throws Exception {
        when(taskExecutionState.getTaskAs(same(IShutdownAwareTask.class)))
                .thenReturn(shutdownAwareTask);

        new IgnoreTaskStrategy().process(taskExecutionState);

        verify(shutdownAwareTask).notifyIgnored();
    }

    @Test(expected = TaskProcessException.class)
    public void Should_wrapExceptionsOccurredOnNotification()
            throws Exception {
        when(taskExecutionState.getTaskAs(same(IShutdownAwareTask.class)))
                .thenReturn(shutdownAwareTask);
        doThrow(ShutdownAwareTaskNotificationException.class)
                .when(shutdownAwareTask).notifyIgnored();

        new IgnoreTaskStrategy().process(taskExecutionState);
    }
}
