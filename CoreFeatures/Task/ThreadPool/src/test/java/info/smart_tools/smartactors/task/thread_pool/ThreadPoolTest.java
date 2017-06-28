package info.smart_tools.smartactors.task.thread_pool;

import info.smart_tools.smartactors.scope.iscope.IScope;
import info.smart_tools.smartactors.task.interfaces.itask.ITask;
import info.smart_tools.smartactors.task.interfaces.itask.exception.TaskExecutionException;
import info.smart_tools.smartactors.scope.scope_provider.ScopeProvider;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Test for {@link ThreadPool}.
 */
public class ThreadPoolTest {
    private ThreadPool threadPool;

    @Before
    public void setUp()
            throws Exception {
        threadPool = new ThreadPool(2);
    }

    // Disable as unlimited queue is used
//    @Test()
    public void Should_interruptThread_WhenThereIsNoMorePlaceInQueue()
            throws Exception {
        ThreadImpl threadMock = mock(ThreadImpl.class);

        threadPool.returnThread(threadMock);

        verify(threadMock).interrupt();
    }

    @Test
    public void Should_executeTasksOnDifferentThreads()
            throws Exception {
        ITask sleepTask = new SleepingTask();

        ITask task1 = spy(sleepTask), task2 = spy(sleepTask);
        ITask task3 = mock(ITask.class);

        assertTrue(threadPool.tryExecute(task1));
        assertTrue(threadPool.tryExecute(task2));
        // Now there is no more free threads.
        assertFalse(threadPool.tryExecute(task3));

        verify(task1, timeout(200)).execute();
        verify(task1, timeout(100)).execute();
    }

    @Test
    public void Should_setThreadsScopeToCreationScope()
            throws Exception {
        AtomicReference<IScope> threadScopeRef = new AtomicReference<>(null);
        ITask task = mock(ITask.class);
        Object scopeId = ScopeProvider.createScope(null);
        IScope scope = ScopeProvider.getScope(scopeId);

        ScopeProvider.setCurrentScope(scope);

        threadPool = new ThreadPool(2);

        doAnswer(invocation -> {
            threadScopeRef.set(ScopeProvider.getCurrentScope());
            return null;
        }).when(task).execute();

        assertTrue(threadPool.tryExecute(task));

        verify(task, timeout(200)).execute();

        assertSame(scope, threadScopeRef.get());
    }

    @Test(timeout = 10000)
    public void Should_terminateThreads()
            throws Exception {
        Object scopeId = ScopeProvider.createScope(null);
        IScope scope = ScopeProvider.getScope(scopeId);

        ScopeProvider.setCurrentScope(scope);

        CyclicBarrier barrier = new CyclicBarrier(2);

        threadPool = new ThreadPool(2);

        assertTrue(threadPool.tryExecute(() -> {
            try {
                barrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                throw new TaskExecutionException(e);
            }
        }));

        threadPool.terminate();

        assertFalse(threadPool.tryExecute(new SleepingTask()));

        // Running task should not be interrupted, so it will reach the barrier.
        barrier.await();

        assertFalse(threadPool.tryExecute(new SleepingTask()));
    }

    private class SleepingTask implements ITask {
        @Override
        public void execute() throws TaskExecutionException {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
