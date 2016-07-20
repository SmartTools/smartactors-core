package info.smart_tools.smartactors.core.message_processor;

import info.smart_tools.smartactors.core.iaction.IAction;
import info.smart_tools.smartactors.core.iaction.exception.ActionExecuteException;
import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.imessage.IMessage;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.iqueue.IQueue;
import info.smart_tools.smartactors.core.itask.ITask;
import info.smart_tools.smartactors.core.itask.exception.TaskExecutionException;
import info.smart_tools.smartactors.core.message_processing.IMessageProcessingSequence;
import info.smart_tools.smartactors.core.message_processing.IMessageReceiver;
import info.smart_tools.smartactors.core.message_processing.exceptions.MessageReceiveException;
import info.smart_tools.smartactors.core.message_processing.exceptions.NoExceptionHandleChainException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.*;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Tests for {@link MessageProcessor}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({IOC.class})
public class MessageProcessorTest {
    private IQueue<ITask> taskQueueMock;
    private IMessageProcessingSequence messageProcessingSequenceMock;
    private IMessage messageMock;
    private IObject contextMock;
    private IObject responseMock;

    private final IKey KEY_FOR_KEY_STORAGE = mock(IKey.class);
    private final IKey KEY_FOR_NEW_IOBJECT = mock(IKey.class);

    @Before
    public void setUp()
            throws Exception {
        taskQueueMock = (IQueue<ITask>) mock(IQueue.class);
        messageProcessingSequenceMock = mock(IMessageProcessingSequence.class);
        messageMock = mock(IMessage.class);
        contextMock = mock(IObject.class);
        responseMock = mock(IObject.class);

        mockStatic(IOC.class);

        when(IOC.getKeyForKeyStorage()).thenReturn(KEY_FOR_KEY_STORAGE);
        when(IOC.resolve(KEY_FOR_KEY_STORAGE, IObject.class)).thenReturn(KEY_FOR_NEW_IOBJECT);
        when(IOC.resolve(KEY_FOR_NEW_IOBJECT)).thenReturn(responseMock).thenReturn(null);
    }

    @Test(expected = InvalidArgumentException.class)
    public void Should_constructorThrow_When_queueIsNull()
            throws Exception {
        assertNotNull(new MessageProcessor(null, messageProcessingSequenceMock));
    }

    @Test(expected = InvalidArgumentException.class)
    public void Should_constructorThrow_When_messageProcessingSequenceIsNull()
            throws Exception {
        assertNotNull(new MessageProcessor(taskQueueMock, null));
    }

    @Test
    public void Should_interruptThread_When_putToQueueIsInterrupted()
            throws Exception {
        IMessageReceiver messageReceiverMock1 = mock(IMessageReceiver.class);

        when(messageProcessingSequenceMock.getCurrentReceiver()).thenReturn(messageReceiverMock1);
        doThrow(new InterruptedException()).when(taskQueueMock).put(any());

        MessageProcessor messageProcessor = new MessageProcessor(taskQueueMock, messageProcessingSequenceMock);

        messageProcessor.process(messageMock, contextMock);
        verify(taskQueueMock).put(same(messageProcessor));
        assertTrue(Thread.interrupted());
    }

    @Test
    public void Should_storeAndReturnMessageAndContextAndSequenceAndCreateResponse()
            throws Exception {
        MessageProcessor messageProcessor = new MessageProcessor(taskQueueMock, messageProcessingSequenceMock);
        messageProcessor.process(messageMock, contextMock);

        assertSame(messageProcessingSequenceMock, messageProcessor.getSequence());
        assertSame(messageMock, messageProcessor.getMessage());
        assertSame(contextMock, messageProcessor.getContext());
        assertSame(responseMock, messageProcessor.getResponse());
    }

    @Test
    public void Should_workWhenNoExceptionsOccurs()
            throws Exception {
        IMessageReceiver messageReceiverMock1 = mock(IMessageReceiver.class);
        IMessageReceiver messageReceiverMock2 = mock(IMessageReceiver.class);
        IObject receiverArgs1 = mock(IObject.class);
        IObject receiverArgs2 = mock(IObject.class);
        ArgumentCaptor<IAction> actionArgumentCaptor = ArgumentCaptor.forClass(IAction.class);

        MessageProcessor messageProcessor = new MessageProcessor(taskQueueMock, messageProcessingSequenceMock);

        messageProcessor.process(messageMock, contextMock);
        verify(taskQueueMock).put(same(messageProcessor));

        when(messageProcessingSequenceMock.getCurrentReceiver()).thenReturn(messageReceiverMock1);
        when(messageProcessingSequenceMock.getCurrentReceiverArguments()).thenReturn(receiverArgs1);
        messageProcessor.execute();
        verify(messageReceiverMock1).receive(same(messageProcessor), same(receiverArgs1), actionArgumentCaptor.capture());

        reset(taskQueueMock, messageProcessingSequenceMock);

        when(messageProcessingSequenceMock.next()).thenReturn(true);
        when(messageProcessingSequenceMock.getCurrentReceiver()).thenReturn(messageReceiverMock2);
        when(messageProcessingSequenceMock.getCurrentReceiverArguments()).thenReturn(receiverArgs2);
        actionArgumentCaptor.getValue().execute(null);
        verify(messageProcessingSequenceMock).next();
        verify(taskQueueMock).put(same(messageProcessor));

        messageProcessor.execute();
        verifyNoMoreInteractions(messageReceiverMock1);
    }

    @Test
    public void Should_throw_WhenExceptionOccursAsynchronouslyAndThereIsNoExceptionalChain()
            throws Exception {
        IMessageReceiver messageReceiverMock1 = mock(IMessageReceiver.class);
        IObject receiverArguments1 = mock(IObject.class);
        ArgumentCaptor<IAction> actionArgumentCaptor = ArgumentCaptor.forClass(IAction.class);
        Throwable exception = new Exception();
        Throwable exception2 = mock(NoExceptionHandleChainException.class);

        MessageProcessor messageProcessor = new MessageProcessor(taskQueueMock, messageProcessingSequenceMock);

        messageProcessor.process(messageMock, contextMock);
        verify(taskQueueMock).put(same(messageProcessor));

        when(messageProcessingSequenceMock.getCurrentReceiver()).thenReturn(messageReceiverMock1);
        when(messageProcessingSequenceMock.getCurrentReceiverArguments()).thenReturn(receiverArguments1);
        doThrow(exception2).when(messageProcessingSequenceMock).catchException(same(exception), same(contextMock));
        messageProcessor.execute();
        verify(messageReceiverMock1).receive(same(messageProcessor), same(receiverArguments1), actionArgumentCaptor.capture());

        try {
            actionArgumentCaptor.getValue().execute(exception);
            fail();
        } catch (ActionExecuteException e) {
            assertSame(exception2, e.getCause());
        }

        verify(messageProcessingSequenceMock).catchException(same(exception), same(contextMock));
    }

    @Test
    public void Should_throw_WhenExceptionOccursSynchronouslyAndThereIsNoExceptionalChain()
            throws Exception {
        IMessageReceiver messageReceiverMock1 = mock(IMessageReceiver.class);
        IObject receiverArguments1 = mock(IObject.class);
        Throwable exception = mock(MessageReceiveException.class);
        Throwable exception2 = mock(NoExceptionHandleChainException.class);

        MessageProcessor messageProcessor = new MessageProcessor(taskQueueMock, messageProcessingSequenceMock);

        messageProcessor.process(messageMock, contextMock);
        verify(taskQueueMock).put(same(messageProcessor));

        when(messageProcessingSequenceMock.getCurrentReceiver()).thenReturn(messageReceiverMock1);
        when(messageProcessingSequenceMock.getCurrentReceiverArguments()).thenReturn(receiverArguments1);
        doThrow(exception).when(messageReceiverMock1).receive(same(messageProcessor), same(receiverArguments1), any());
        doThrow(exception2).when(messageProcessingSequenceMock).catchException(same(exception), same(contextMock));

        try {
            messageProcessor.execute();
            fail();
        } catch (TaskExecutionException e) {
            assertSame(exception2, e.getCause());
        }

        verify(messageProcessingSequenceMock).catchException(same(exception), same(contextMock));
    }

    @Test
    public void Should_notThrow_WhenExceptionOccursAsynchronouslyAndThereIsExceptionalChain()
            throws Exception {
        IMessageReceiver messageReceiverMock1 = mock(IMessageReceiver.class);
        IObject receiverArguments1 = mock(IObject.class);
        ArgumentCaptor<IAction> actionArgumentCaptor = ArgumentCaptor.forClass(IAction.class);
        Throwable exception = new Exception();

        MessageProcessor messageProcessor = new MessageProcessor(taskQueueMock, messageProcessingSequenceMock);

        messageProcessor.process(messageMock, contextMock);
        verify(taskQueueMock).put(same(messageProcessor));
        reset(taskQueueMock);

        when(messageProcessingSequenceMock.getCurrentReceiver()).thenReturn(messageReceiverMock1);
        when(messageProcessingSequenceMock.getCurrentReceiverArguments()).thenReturn(receiverArguments1);
        messageProcessor.execute();
        verify(messageReceiverMock1).receive(same(messageProcessor), same(receiverArguments1), actionArgumentCaptor.capture());

        actionArgumentCaptor.getValue().execute(exception);

        verify(messageProcessingSequenceMock).catchException(same(exception), same(contextMock));
        verify(taskQueueMock).put(same(messageProcessor));
    }
}
