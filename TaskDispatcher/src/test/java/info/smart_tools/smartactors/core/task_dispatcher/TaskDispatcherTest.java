package info.smart_tools.smartactors.core.task_dispatcher;

import info.smart_tools.smartactors.core.iqueue.IQueue;
import info.smart_tools.smartactors.core.itask.ITask;
import info.smart_tools.smartactors.core.ithread_pool.IThreadPool;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

/**
 * Test for {@link TaskDispatcher}.
 */
public class TaskDispatcherTest {
    private IQueue<ITask> queueMock;
    private IThreadPool threadPoolMock;

    @Before
    public void setUp()
            throws Exception {
        queueMock = (IQueue<ITask>) mock(IQueue.class);
        threadPoolMock = mock(IThreadPool.class);
    }

    @Test
    public void Should_setCallbackWhenStarted_And_removeItWhenStopped()
            throws Exception {
        ArgumentCaptor<Runnable> callbackCaptor = ArgumentCaptor.forClass(Runnable.class);

        TaskDispatcher dispatcher = spy(new TaskDispatcher(queueMock, threadPoolMock, 1000L, 8));

        verifyZeroInteractions(queueMock, threadPoolMock);

        dispatcher.start();
        verify(queueMock).addNewItemCallback(callbackCaptor.capture());

        dispatcher.stop();
        verify(queueMock).removeNewItemCallback(same(callbackCaptor.getValue()));

        verify(dispatcher, never()).tryStartNewThread();

        callbackCaptor.getValue().run();
        //verify(dispatcher, times(1)).tryStartNewThread();
    }

    @Test
    public void Should_gettersReturnThreadPoolAndQueue()
            throws Exception {
        TaskDispatcher dispatcher = new TaskDispatcher(queueMock, threadPoolMock, 1000L, 8);

        assertSame(queueMock, dispatcher.getTaskQueue());
        assertSame(threadPoolMock, dispatcher.getThreadPool());
    }

    @Test
    public void Should_tryToStartNewThreadIfNecessary()
            throws Exception {
        ArgumentCaptor<ITask> taskCaptor = ArgumentCaptor.forClass(ITask.class);

        TaskDispatcher dispatcher = new TaskDispatcher(queueMock, threadPoolMock, 1000L, 8);

        for (int i = 0; i < 8; i++) {
            dispatcher.notifyThreadStart();
            dispatcher.notifyTaskTaken();
        }
        dispatcher.notifyThreadStop();

        dispatcher.tryStartNewThread();

        verify(threadPoolMock).tryExecute(taskCaptor.capture());
        assertNotNull(taskCaptor.getValue());
        assertTrue(taskCaptor.getValue() instanceof ExecutionTask);
    }

    @Test
    public void Should_notTryToStartNewThreadIfThereIsTooManyThreads()
            throws Exception {
        TaskDispatcher dispatcher = new TaskDispatcher(queueMock, threadPoolMock, 1000000000L, 8);

        for (int i = 0; i < 8; i++) {
            dispatcher.notifyThreadStart();
            dispatcher.notifyTaskTaken();
        }

        dispatcher.tryStartNewThread();

        verify(threadPoolMock, never()).tryExecute(any());
    }
}
