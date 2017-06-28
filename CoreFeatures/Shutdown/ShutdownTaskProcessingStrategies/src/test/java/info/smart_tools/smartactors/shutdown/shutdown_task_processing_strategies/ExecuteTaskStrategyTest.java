package info.smart_tools.smartactors.shutdown.shutdown_task_processing_strategies;

import info.smart_tools.smartactors.task.itask_execution_state.ITaskExecutionState;
import org.junit.Test;

import static org.mockito.Mockito.*;

/**
 * Test for {@link ExecuteTaskStrategy}.
 */
public class ExecuteTaskStrategyTest {
    @Test
    public void Should_executeTask()
            throws Exception {
        ITaskExecutionState taskExecutionState = mock(ITaskExecutionState.class);

        new ExecuteTaskStrategy().process(taskExecutionState);

        verify(taskExecutionState).execute();
        verifyNoMoreInteractions(taskExecutionState);
    }
}
