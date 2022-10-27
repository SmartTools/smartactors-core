package info.smart_tools.smartactors.task.task_queue_decorator;

import info.smart_tools.smartactors.class_management.module_manager.ModuleManager;
import info.smart_tools.smartactors.scope.iscope.IScope;
import info.smart_tools.smartactors.scope.iscope_provider_container.exception.ScopeProviderException;
import info.smart_tools.smartactors.scope.scope_provider.ScopeProvider;
import info.smart_tools.smartactors.task.interfaces.iqueue.IQueue;
import info.smart_tools.smartactors.task.interfaces.itask.ITask;
import info.smart_tools.smartactors.task.non_blocking_queue.NonBlockingQueue;
import info.smart_tools.smartactors.task.task_queue_decorator.TaskQueueDecorator;
import org.junit.Before;
import org.junit.Test;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

/**
 * Test for {@link TaskQueueDecorator}.
 */
public class TaskQueueDecoratorTest {

    @Before
    public void init()
            throws ScopeProviderException {
        ModuleManager.setCurrentModule(ModuleManager.getModuleById(ModuleManager.coreId));
        Object scopeKey = ScopeProvider.createScope(null);
        ScopeProvider.setCurrentScope(ScopeProvider.getScope(scopeKey));
    }

    @Test
    public void Should_takeFromUnderlying()
            throws Exception {
        IQueue underlying = mock(IQueue.class);
        IQueue queue = new TaskQueueDecorator(underlying);
        queue.take();
        verify(underlying).take();
    }

    @Test
    public void Should_CallMethodsOfUnderlyingQueue()
            throws Exception {
        IQueue underlying = mock(IQueue.class);
        ITask object1 = mock(ITask.class), object2 = mock(ITask.class), object3 = mock(ITask.class);

        when(underlying.tryTake()).thenReturn(object3);

        IQueue queue = new TaskQueueDecorator(underlying);

        queue.put(object1);

        verify(underlying).put(any());

        assertSame(object3, queue.tryTake());
    }

    @Test
    public void Should_CallCallbacksWhenNewItemAdded()
            throws Exception {
        IQueue underlying = new NonBlockingQueue((new ConcurrentLinkedQueue<>()));

        Runnable callback1 = mock(Runnable.class), callback2 = mock(Runnable.class);

        IQueue queue = new TaskQueueDecorator(underlying);

        queue.addNewItemCallback(callback1);
        queue.addNewItemCallback(callback2);

        verifyZeroInteractions(callback1, callback2);

        queue.put(mock(ITask.class));

        verify(callback1).run();
        verify(callback2).run();
    }

    @Test
    public void Should_callCallbackImmediately_When_thereAlreadyAreElementsInQueue()
            throws Exception {
        IQueue underlying = new NonBlockingQueue((new ConcurrentLinkedQueue<>()));

        Runnable callback = mock(Runnable.class);

        //when(underlying.isEmpty()).thenReturn(false);

        IQueue queue = new TaskQueueDecorator(underlying);

        queue.put(mock(ITask.class));

        queue.addNewItemCallback(callback);

        verify(callback).run();

        ITask task = (ITask)queue.tryTake();
        task.execute();
    }

    @Test
    public void Should_notCallCallbackRemoved_When_itIsRemoved()
            throws Exception {
        IQueue underlying = new NonBlockingQueue((new ConcurrentLinkedQueue<>()));

        Runnable callback = mock(Runnable.class);

        IQueue queue = new TaskQueueDecorator(underlying);

        queue.addNewItemCallback(callback);
        queue.removeNewItemCallback(callback);

        queue.put(mock(ITask.class));

        verify(callback, never()).run();
    }
}