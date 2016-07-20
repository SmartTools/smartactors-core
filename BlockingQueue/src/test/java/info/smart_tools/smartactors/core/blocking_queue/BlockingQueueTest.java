package info.smart_tools.smartactors.core.blocking_queue;

import info.smart_tools.smartactors.core.iqueue.IQueue;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Test for {@link BlockingQueue}
 */
public class BlockingQueueTest {
    @Test
    public void Should_BlockingQueueCallMethodsOfUnderlyingQueue()
            throws Exception {
        java.util.concurrent.BlockingQueue underlying = mock(java.util.concurrent.BlockingQueue.class);
        Object object1 = new Object(), object2 = new Object(), object3 = new Object();

        when(underlying.take()).thenReturn(object2);
        when(underlying.poll()).thenReturn(object3);

        IQueue queue = new BlockingQueue<>(underlying);

        queue.put(object1);

        verify(underlying).put(same(object1));

        assertSame(object2, queue.take());
        assertSame(object3, queue.tryTake());
    }

    @Test
    public void Should_CallCallbacksWhenNewItemAdded()
            throws Exception {
        java.util.concurrent.BlockingQueue underlying = mock(java.util.concurrent.BlockingQueue.class);

        Runnable callback1 = mock(Runnable.class), callback2 = mock(Runnable.class);

        when(underlying.isEmpty()).thenReturn(true);

        IQueue queue = new BlockingQueue<>(underlying);

        queue.addNewItemCallback(callback1);
        queue.addNewItemCallback(callback2);

        verifyZeroInteractions(callback1, callback2);

        queue.put(new Object());

        verify(callback1).run();
        verify(callback2).run();
    }

    @Test
    public void Should_callCallbackImmediately_When_thereAlreadyAreElementsInQueue()
            throws Exception {
        java.util.concurrent.BlockingQueue underlying = mock(java.util.concurrent.BlockingQueue.class);

        Runnable callback = mock(Runnable.class);

        when(underlying.isEmpty()).thenReturn(false);

        IQueue queue = new BlockingQueue<>(underlying);

        queue.addNewItemCallback(callback);

        verify(callback).run();
    }

    @Test
    public void Should_notCallCallbackRemoved_When_itIsRemoved()
            throws Exception {
        java.util.concurrent.BlockingQueue underlying = mock(java.util.concurrent.BlockingQueue.class);

        Runnable callback = mock(Runnable.class);

        when(underlying.isEmpty()).thenReturn(true);

        IQueue queue = new BlockingQueue<>(underlying);

        queue.addNewItemCallback(callback);
        queue.removeNewItemCallback(callback);

        queue.put(new Object());

        verify(callback, never()).run();
    }
}
