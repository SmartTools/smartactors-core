package info.smart_tools.smartactors.task.task_dispatcher;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.task.imanaged_task.IManagedTask;
import info.smart_tools.smartactors.task.interfaces.iqueue.IQueue;
import info.smart_tools.smartactors.task.interfaces.itask.ITask;
import info.smart_tools.smartactors.task.interfaces.ithread_pool.IThreadPool;
import info.smart_tools.smartactors.task.itask_execution_state.ITaskExecutionState;
import info.smart_tools.smartactors.task.itask_preprocess_strategy.ITaskProcessStrategy;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

/**
 * Test for {@link ExecutionTaskWithStrategy}.
 */
public class ExecutionTaskWithStrategyTest {
    private TaskDispatcher dispatcherMock;
    private IQueue<ITask> queueMock;
    private IThreadPool threadPoolMock;
    private ITaskProcessStrategy processStrategyMock;

    @Before
    public void setUp()
            throws Exception {
        dispatcherMock = mock(TaskDispatcher.class);
        queueMock = (IQueue<ITask>) mock(IQueue.class);
        threadPoolMock = mock(IThreadPool.class);

        when(dispatcherMock.getTaskQueue()).thenReturn(queueMock);
        when(dispatcherMock.getThreadPool()).thenReturn(threadPoolMock);

        processStrategyMock = mock(ITaskProcessStrategy.class);
    }


    @Test(timeout = 1000L)
    public void Should_exit_When_thereIsNoTask()
            throws Exception {
        ExecutionTaskWithStrategy executionTask = new ExecutionTaskWithStrategy(dispatcherMock, processStrategyMock);
        when(dispatcherMock.getExecutionTask()).thenReturn(executionTask);

        when(queueMock.tryTake()).thenReturn(null);

        executionTask.execute();
    }

    @Test(timeout = 1000L)
    public void Should_exit_When_threadIsInterrupted()
            throws Exception {
        ExecutionTaskWithStrategy executionTask = new ExecutionTaskWithStrategy(dispatcherMock, processStrategyMock);
        when(dispatcherMock.getExecutionTask()).thenReturn(executionTask);

        Thread.currentThread().interrupt();

        executionTask.execute();

        verify(queueMock, never()).tryTake();

        assertTrue(Thread.interrupted());
    }

    @Test(timeout = 1000L)
    public void Should_letStrategyExecuteTask()
            throws Exception {
        ITask taskMock = mock(ITask.class);

        when(queueMock.tryTake())
                .thenReturn(taskMock)
                .thenReturn(null);

        ExecutionTaskWithStrategy executionTask = new ExecutionTaskWithStrategy(dispatcherMock, processStrategyMock);
        when(dispatcherMock.getExecutionTask()).thenReturn(executionTask);

        doAnswer(invocation -> {
            invocation.getArgumentAt(0, ITaskExecutionState.class).execute();
            return null;
        }).when(processStrategyMock).process(any());

        executionTask.execute();

        verify(taskMock).execute();
    }

    @Test(timeout = 1000L)
    public void Should_letStrategyAccessTaskClass()
            throws Exception {
        ITask taskMock = mock(ITask.class);

        when(queueMock.tryTake())
                .thenReturn(taskMock)
                .thenReturn(null);

        ExecutionTaskWithStrategy executionTask = new ExecutionTaskWithStrategy(dispatcherMock, processStrategyMock);
        when(dispatcherMock.getExecutionTask()).thenReturn(executionTask);

        doAnswer(invocation -> {
            Class c = invocation.getArgumentAt(0, ITaskExecutionState.class).getTaskClass();
            assertSame(taskMock.getClass(), c);
            return null;
        }).when(processStrategyMock).process(any());

        executionTask.execute();
    }

    @Test(timeout = 1000L)
    public void Should_letStrategyAccessManagedTask()
            throws Exception {
        ITask taskMock = mock(ITask.class);

        when(queueMock.tryTake())
                .thenReturn(taskMock)
                .thenReturn(null);

        ExecutionTaskWithStrategy executionTask = new ExecutionTaskWithStrategy(dispatcherMock, processStrategyMock);
        when(dispatcherMock.getExecutionTask()).thenReturn(executionTask);

        doAnswer(invocation -> {
            try {
                invocation.getArgumentAt(0, ITaskExecutionState.class).getTaskAs(IManagedTask.class);
                fail();
            } catch (InvalidArgumentException ignore) {}
            return null;
        }).when(processStrategyMock).process(any());

        executionTask.execute();

        IManagedTask managedTaskMock = mock(IManagedTask.class),
                managedTaskMock1 = mock(IManagedTask.class);

        when(managedTaskMock.getAs(IManagedTask.class)).thenReturn(managedTaskMock1);

        when(queueMock.tryTake())
                .thenReturn(managedTaskMock)
                .thenReturn(null);

        doAnswer(invocation -> {
            IManagedTask mt = invocation.getArgumentAt(0, ITaskExecutionState.class).getTaskAs(IManagedTask.class);
            assertSame(managedTaskMock1, mt);
            return null;
        }).when(processStrategyMock).process(any());

        executionTask.execute();
        verify(managedTaskMock).getAs(IManagedTask.class);
    }

    @Test(timeout = 100L)
    public void Should_exit_When_currentExecutionTaskChanges()
            throws Exception {
        ExecutionTaskWithStrategy executionTask = new ExecutionTaskWithStrategy(dispatcherMock, processStrategyMock);
        ITask nextExecTsk = mock(ExecutionTask.class);
        when(dispatcherMock.getExecutionTask()).thenReturn(nextExecTsk);

        executionTask.execute();

        verify(queueMock, never()).tryTake();

        verify(threadPoolMock).tryExecute(same(nextExecTsk));
    }
}
