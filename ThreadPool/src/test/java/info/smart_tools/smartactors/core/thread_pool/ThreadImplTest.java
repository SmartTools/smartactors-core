package info.smart_tools.smartactors.core.thread_pool;

import info.smart_tools.smartactors.core.itask.ITask;
import info.smart_tools.smartactors.core.itask.exception.TaskExecutionException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Test for {@link ThreadImpl}.
 */
public class ThreadImplTest {
    ThreadPool threadPoolMock;
    ThreadImpl thread;

    @Before
    public void setUp()
            throws Exception {
        threadPoolMock = mock(ThreadPool.class);
        thread = new ThreadImpl(threadPoolMock);
    }

    @After
    public void tearDown()
            throws Exception {
        if (null != thread) {
            thread.interrupt();
        }
    }

    @Test
    public void Should_executeTaskInSeparateThread()
            throws Exception {
        ITask taskMock = mock(ITask.class);

        thread.execute(taskMock);

        verify(taskMock, timeout(1000)).execute();
    }

    @Test(expected = TaskExecutionException.class)
    public void Should_throwWhenAnotherTaskIsBeingExecuted()
            throws Exception {
        thread.execute(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                thread.interrupt();
            }
        });

        thread.execute(mock(ITask.class));
    }

    @Test(expected = TaskExecutionException.class)
    public void Should_throwWhenThreadIsNotAlive()
            throws Exception {
        ITask taskMock = mock(ITask.class);

        thread.interrupt();
        Thread.sleep(100);
        thread.execute(taskMock);

        verify(taskMock, timeout(100).times(0)).execute();
    }

    @Test
    public void Should_ignoreExceptionsFromTask()
            throws Exception {
        ITask taskMock1 = mock(ITask.class), taskMock2 = mock(ITask.class);

        doThrow(new TaskExecutionException("Whoops!")).when(taskMock1).execute();

        thread.execute(taskMock1);
        verify(taskMock1, timeout(100)).execute();

        Thread.sleep(100);

        thread.execute(taskMock2);
        verify(taskMock2, timeout(100)).execute();
    }
}
