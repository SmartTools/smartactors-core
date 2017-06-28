package info.smart_tools.smartactors.shutdown.shutdown_task_processing_strategies;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.shutdown.ishutdown_aware_task.IShutdownAwareTask;
import info.smart_tools.smartactors.task.itask_execution_state.ITaskExecutionState;
import info.smart_tools.smartactors.task.itask_preprocess_strategy.ITaskProcessStrategy;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Test for {@link LimitTrialCountStrategy}.
 */
public class LimitTrialCountStrategyTest {
    private ITaskExecutionState executionState;
    private IShutdownAwareTask shutdownAwareTask;

    @Before
    public void setUp()
            throws Exception {
        executionState = mock(ITaskExecutionState.class);
        shutdownAwareTask = mock(IShutdownAwareTask.class);

        doAnswer(invocation -> {
            when(shutdownAwareTask.getShutdownStatus()).thenReturn(invocation.getArgumentAt(0, Object.class));
            return null;
        }).when(shutdownAwareTask).setShutdownStatus(any());

        when(executionState.getTaskAs(IShutdownAwareTask.class)).thenReturn(shutdownAwareTask);
    }

    @Test(expected = InvalidArgumentException.class)
    public void Should_throwWhenConstructorParametersAreInvalid1()
            throws Exception {
        assertNotNull(new LimitTrialCountStrategy(0, 0));
    }

    @Test(expected = InvalidArgumentException.class)
    public void Should_throwWhenConstructorParametersAreInvalid2()
            throws Exception {
        assertNotNull(new LimitTrialCountStrategy(5, -1));
    }

    @Test(expected = InvalidArgumentException.class)
    public void Should_throwWhenConstructorParametersAreInvalid3()
            throws Exception {
        assertNotNull(new LimitTrialCountStrategy(5, 6));
    }

    @Test
    public void Should_NotifyAndExecuteTask()
            throws Exception {
        ITaskProcessStrategy strategy = new LimitTrialCountStrategy(4,2);

        strategy.process(executionState);
        strategy.process(executionState);
        verify(executionState, times(2)).execute();
        verify(shutdownAwareTask, times(0)).notifyShuttingDown();

        strategy.process(executionState);
        strategy.process(executionState);
        verify(executionState, times(4)).execute();
        verify(shutdownAwareTask, times(2)).notifyShuttingDown();
        verify(shutdownAwareTask, times(0)).notifyIgnored();

        strategy.process(executionState);
        verify(executionState, times(4)).execute();
        verify(shutdownAwareTask, times(2)).notifyShuttingDown();
        verify(shutdownAwareTask, times(1)).notifyIgnored();
    }
}
