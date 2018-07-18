package info.smart_tools.smartactors.endpoint_components_generic.asynchronous_unordered_message_handler;

import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandler;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandlerCallback;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.exception.MessageHandlerException;
import info.smart_tools.smartactors.task.interfaces.iqueue.IQueue;
import info.smart_tools.smartactors.task.interfaces.itask.ITask;
import info.smart_tools.smartactors.task.interfaces.itask.exception.TaskExecutionException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

public class AsynchronousUnorderedMessageHandlerTest {
    private IMessageContext messageContext;
    private IMessageHandlerCallback callback;
    private IQueue taskQueue;
    private ArgumentCaptor<ITask> taskCaptor;

    private IMessageHandler handler;

    @Before public void setUp() throws Exception {
        messageContext = mock(IMessageContext.class);
        callback = mock(IMessageHandlerCallback.class);
        taskQueue = mock(IQueue.class);
        taskCaptor = ArgumentCaptor.forClass(ITask.class);

        handler = new AsynchronousUnorderedMessageHandler(taskQueue);
    }

    @Test public void Should_sendExecutionTaskToQueue() throws Exception {
        handler.handle(callback, messageContext);

        verifyNoMoreInteractions(callback, messageContext);

        verify(taskQueue).put(taskCaptor.capture());

        taskCaptor.getValue().execute();

        verify(callback).handle(same(messageContext));
    }

    @Test(expected = TaskExecutionException.class) public void Should_taskThrowWhenCallbackThrows() throws Exception {
        doThrow(MessageHandlerException.class).when(callback).handle(any());

        handler.handle(callback, messageContext);

        verifyNoMoreInteractions(callback, messageContext);

        verify(taskQueue).put(taskCaptor.capture());

        taskCaptor.getValue().execute();
    }


    @Test public void Should_interruptThreadWhenTaskQueueThrows() throws Exception {
        doThrow(InterruptedException.class).when(taskQueue).put(any());

        handler.handle(callback, messageContext);

        assertTrue(Thread.interrupted());
    }
}
