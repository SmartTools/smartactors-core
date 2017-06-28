package info.smart_tools.smartactors.task.task_dispatcher;

import info.smart_tools.smartactors.task.interfaces.iqueue.IQueue;
import info.smart_tools.smartactors.task.interfaces.itask.ITask;
import info.smart_tools.smartactors.task.interfaces.ithread_pool.IThreadPool;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Test for {@link ExecutionTask}
 */
public class ExecutionTaskTest {
    private TaskDispatcher dispatcherMock;
    private IQueue<ITask> queueMock;
    private IThreadPool threadPoolMock;

    @Before
    public void setUp()
            throws Exception {
        dispatcherMock = mock(TaskDispatcher.class);
        queueMock = (IQueue<ITask>) mock(IQueue.class);
        threadPoolMock = mock(IThreadPool.class);

        when(dispatcherMock.getTaskQueue()).thenReturn(queueMock);
        when(dispatcherMock.getThreadPool()).thenReturn(threadPoolMock);
    }

    @Test(timeout = 100L)
    public void Should_exit_When_thereIsNoTask()
            throws Exception {
        ExecutionTask executionTask = new ExecutionTask(dispatcherMock);
        when(dispatcherMock.getExecutionTask()).thenReturn(executionTask);

        when(queueMock.tryTake()).thenReturn(null);

        executionTask.execute();
    }

    @Test(timeout = 100L)
    public void Should_exit_When_threadIsInterrupted()
            throws Exception {
        ExecutionTask executionTask = new ExecutionTask(dispatcherMock);
        when(dispatcherMock.getExecutionTask()).thenReturn(executionTask);

        Thread.currentThread().interrupt();

        executionTask.execute();

        verify(queueMock, never()).tryTake();

        assertTrue(Thread.interrupted());
    }

    @Test(timeout = 100L)
    public void Should_exit_When_currentExecutionTaskChanges()
            throws Exception {
        ExecutionTask executionTask = new ExecutionTask(dispatcherMock);
        ITask nextExecTsk = mock(ExecutionTask.class);
        when(dispatcherMock.getExecutionTask()).thenReturn(nextExecTsk);

        executionTask.execute();

        verify(queueMock, never()).tryTake();

        verify(threadPoolMock).tryExecute(same(nextExecTsk));
    }

    @Test(timeout = 100L)
    public void Should_takeAndExecuteTasks()
            throws Exception {
        ITask taskMock = mock(ITask.class);

        when(queueMock.tryTake())
                .thenReturn(taskMock)
                .thenReturn(null);

        ExecutionTask executionTask = new ExecutionTask(dispatcherMock);
        when(dispatcherMock.getExecutionTask()).thenReturn(executionTask);

        executionTask.execute();

        verify(taskMock).execute();
    }

    @Test(timeout = 100L)
    public void Should_executeItselfOnNewThreadIfCanGetOne()
            throws Exception {
        ITask taskMock = mock(ITask.class);

        ExecutionTask executionTask = new ExecutionTask(dispatcherMock);

        when(dispatcherMock.getExecutionTask()).thenReturn(executionTask);

        when(queueMock.tryTake())
                .thenReturn(taskMock)
                .thenReturn(null);

        when(threadPoolMock.tryExecute(same(executionTask)))
                .thenReturn(true)
                .thenReturn(false);

        executionTask.execute();

        verify(taskMock).execute();
        verify(threadPoolMock).tryExecute(same(executionTask));
    }
}
