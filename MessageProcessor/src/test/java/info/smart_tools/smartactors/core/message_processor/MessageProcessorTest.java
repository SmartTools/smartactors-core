package info.smart_tools.smartactors.core.message_processor;

import info.smart_tools.smartactors.core.field_name.FieldName;
import info.smart_tools.smartactors.core.ifield_name.IFieldName;
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
import info.smart_tools.smartactors.core.message_processing.exceptions.AsynchronousOperationException;
import info.smart_tools.smartactors.core.message_processing.exceptions.MessageReceiveException;
import info.smart_tools.smartactors.core.message_processing.exceptions.NoExceptionHandleChainException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
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
    private IObject environmentMock;
    private IObject configurationMock;

    private final IKey KEY_FOR_KEY_STORAGE = mock(IKey.class);
    private final IKey KEY_FOR_NEW_IOBJECT = mock(IKey.class);
    private final IKey KEY_FOR_FIELD_NAME = mock(IKey.class);

    @Before
    public void setUp()
            throws Exception {
        taskQueueMock = (IQueue<ITask>) mock(IQueue.class);
        messageProcessingSequenceMock = mock(IMessageProcessingSequence.class);
        messageMock = mock(IMessage.class);
        contextMock = mock(IObject.class);
        responseMock = mock(IObject.class);
        environmentMock = mock(IObject.class);
        configurationMock = mock(IObject.class);

        mockStatic(IOC.class);

        when(IOC.getKeyForKeyStorage()).thenReturn(KEY_FOR_KEY_STORAGE);
        when(IOC.resolve(KEY_FOR_KEY_STORAGE, IObject.class.getCanonicalName())).thenReturn(KEY_FOR_NEW_IOBJECT);
        when(IOC.resolve(KEY_FOR_NEW_IOBJECT))
                .thenReturn(environmentMock);
        when(IOC.resolve(KEY_FOR_KEY_STORAGE, IFieldName.class.getCanonicalName())).thenReturn(KEY_FOR_FIELD_NAME);
        when(IOC.resolve(same(KEY_FOR_FIELD_NAME), any()))
                .thenAnswer(invocationOnMock -> new FieldName((String) invocationOnMock.getArguments()[1]));
    }

    @Test(expected = InvalidArgumentException.class)
    public void Should_constructorThrow_When_queueIsNull()
            throws Exception {
        assertNotNull(new MessageProcessor(null, messageProcessingSequenceMock, configurationMock));
    }

    @Test(expected = InvalidArgumentException.class)
    public void Should_constructorThrow_When_messageProcessingSequenceIsNull()
            throws Exception {
        assertNotNull(new MessageProcessor(taskQueueMock, null, configurationMock));
    }

    @Test(expected = InvalidArgumentException.class)
    public void Should_constructorThrow_When_configIsNull()
            throws Exception {
        assertNotNull(new MessageProcessor(taskQueueMock, messageProcessingSequenceMock, null));
    }

    @Test(expected = InvalidArgumentException.class)
    public void Should_setConfigThrow_When_ConfigIsNull()
            throws Exception {
        new MessageProcessor(taskQueueMock, messageProcessingSequenceMock, configurationMock).setConfig(null);
    }

    @Test
    public void Should_updateConfigurationInEnvironmentStartOfProcessing()
            throws Exception {
        IObject configurationMock2 = mock(IObject.class);
        MessageProcessor messageProcessor = new MessageProcessor(taskQueueMock, messageProcessingSequenceMock, configurationMock);

        messageProcessor.process(messageMock, contextMock);

        verify(environmentMock).setValue(eq(IOC.resolve(KEY_FOR_FIELD_NAME, "config")), same(configurationMock));

        messageProcessor.setConfig(configurationMock2);

        messageProcessor.process(messageMock, contextMock);

        verify(environmentMock).setValue(eq(IOC.resolve(KEY_FOR_FIELD_NAME, "config")), same(configurationMock2));
    }

    @Test
    public void Should_interruptThread_When_putToQueueIsInterrupted()
            throws Exception {
        IMessageReceiver messageReceiverMock1 = mock(IMessageReceiver.class);

        when(messageProcessingSequenceMock.getCurrentReceiver()).thenReturn(messageReceiverMock1);
        doThrow(new InterruptedException()).when(taskQueueMock).put(any());

        MessageProcessor messageProcessor = new MessageProcessor(taskQueueMock, messageProcessingSequenceMock, configurationMock);

        when(IOC.resolve(KEY_FOR_NEW_IOBJECT))
                .thenReturn(responseMock);

        messageProcessor.process(messageMock, contextMock);
        verify(taskQueueMock).put(same(messageProcessor));
        assertTrue(Thread.interrupted());
    }

    @Test
    public void Should_storeAndReturnMessageAndContextAndSequenceAndCreateResponse()
            throws Exception {
        MessageProcessor messageProcessor = new MessageProcessor(taskQueueMock, messageProcessingSequenceMock, configurationMock);

        when(IOC.resolve(KEY_FOR_NEW_IOBJECT))
                .thenReturn(responseMock);

        messageProcessor.process(messageMock, contextMock);

        assertSame(messageProcessingSequenceMock, messageProcessor.getSequence());
        assertSame(messageMock, messageProcessor.getMessage());
        assertSame(contextMock, messageProcessor.getContext());
        assertSame(responseMock, messageProcessor.getResponse());
        assertSame(environmentMock, messageProcessor.getEnvironment());
    }

    @Test
    public void Should_workWhenNoExceptionsOccurs()
            throws Exception {
        IMessageReceiver messageReceiverMock1 = mock(IMessageReceiver.class);
        IMessageReceiver messageReceiverMock2 = mock(IMessageReceiver.class);
        IObject receiverArgs1 = mock(IObject.class);
        IObject receiverArgs2 = mock(IObject.class);

        MessageProcessor messageProcessor = new MessageProcessor(taskQueueMock, messageProcessingSequenceMock, configurationMock);

        when(IOC.resolve(KEY_FOR_NEW_IOBJECT))
                .thenReturn(responseMock);

        messageProcessor.process(messageMock, contextMock);
        verify(taskQueueMock).put(same(messageProcessor));

        when(messageProcessingSequenceMock.getCurrentReceiver()).thenReturn(messageReceiverMock1);
        when(messageProcessingSequenceMock.getCurrentReceiverArguments()).thenReturn(receiverArgs1);

        doAnswer(invocationOnMock -> {
            messageProcessor.pauseProcess();
            return null;
        }).when(messageReceiverMock1).receive(same(messageProcessor));

        messageProcessor.execute();
        verify(messageReceiverMock1).receive(same(messageProcessor));

        reset(taskQueueMock, messageProcessingSequenceMock);

        when(messageProcessingSequenceMock.next()).thenReturn(true);
        when(messageProcessingSequenceMock.getCurrentReceiver()).thenReturn(messageReceiverMock2);
        when(messageProcessingSequenceMock.getCurrentReceiverArguments()).thenReturn(receiverArgs2);

        messageProcessor.continueProcess(null);

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
        Throwable exception = new Exception();
        Throwable exception2 = mock(NoExceptionHandleChainException.class);

        MessageProcessor messageProcessor = new MessageProcessor(taskQueueMock, messageProcessingSequenceMock, configurationMock);

        when(IOC.resolve(KEY_FOR_NEW_IOBJECT))
                .thenReturn(responseMock);

        messageProcessor.process(messageMock, contextMock);
        verify(taskQueueMock).put(same(messageProcessor));

        doAnswer(invocationOnMock -> {
            messageProcessor.pauseProcess();
            return null;
        }).when(messageReceiverMock1).receive(same(messageProcessor));

        when(messageProcessingSequenceMock.getCurrentReceiver()).thenReturn(messageReceiverMock1);
        when(messageProcessingSequenceMock.getCurrentReceiverArguments()).thenReturn(receiverArguments1);
        doThrow(exception2).when(messageProcessingSequenceMock).catchException(same(exception), same(contextMock));
        messageProcessor.execute();
        verify(messageReceiverMock1).receive(same(messageProcessor));

        try {
            messageProcessor.continueProcess(exception);
            fail();
        } catch (AsynchronousOperationException e) {
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

        MessageProcessor messageProcessor = new MessageProcessor(taskQueueMock, messageProcessingSequenceMock, configurationMock);

        when(IOC.resolve(KEY_FOR_NEW_IOBJECT))
                .thenReturn(responseMock);

        messageProcessor.process(messageMock, contextMock);
        verify(taskQueueMock).put(same(messageProcessor));

        when(messageProcessingSequenceMock.getCurrentReceiver()).thenReturn(messageReceiverMock1);
        when(messageProcessingSequenceMock.getCurrentReceiverArguments()).thenReturn(receiverArguments1);
        doThrow(exception).when(messageReceiverMock1).receive(same(messageProcessor));
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
        Throwable exception = new Exception();

        MessageProcessor messageProcessor = new MessageProcessor(taskQueueMock, messageProcessingSequenceMock, configurationMock);

        when(IOC.resolve(KEY_FOR_NEW_IOBJECT))
                .thenReturn(responseMock);

        messageProcessor.process(messageMock, contextMock);
        verify(taskQueueMock).put(same(messageProcessor));
        reset(taskQueueMock);

        doAnswer(invocationOnMock -> {
            messageProcessor.pauseProcess();
            return null;
        }).when(messageReceiverMock1).receive(same(messageProcessor));

        when(messageProcessingSequenceMock.getCurrentReceiver()).thenReturn(messageReceiverMock1);
        when(messageProcessingSequenceMock.getCurrentReceiverArguments()).thenReturn(receiverArguments1);
        when(messageProcessingSequenceMock.next()).thenReturn(true).thenReturn(false);
        messageProcessor.execute();
        verify(messageReceiverMock1).receive(same(messageProcessor));

        messageProcessor.continueProcess(exception);

        verify(messageProcessingSequenceMock).catchException(same(exception), same(contextMock));
        verify(taskQueueMock).put(same(messageProcessor));
    }

    @Test(expected = AsynchronousOperationException.class)
    public void Should_throw_WhenOrderOfAsynchronousOperationControlMethodsIsNotCorrect()
            throws Exception {
        IMessageReceiver messageReceiverMock1 = mock(IMessageReceiver.class);
        IObject receiverArguments1 = mock(IObject.class);

        MessageProcessor messageProcessor = new MessageProcessor(taskQueueMock, messageProcessingSequenceMock, configurationMock);

        when(IOC.resolve(KEY_FOR_NEW_IOBJECT))
                .thenReturn(responseMock);

        messageProcessor.process(messageMock, contextMock);
        verify(taskQueueMock).put(same(messageProcessor));
        reset(taskQueueMock);

        doAnswer(invocationOnMock -> {
            messageProcessor.pauseProcess();
            return null;
        }).when(messageReceiverMock1).receive(same(messageProcessor));

        when(messageProcessingSequenceMock.getCurrentReceiver()).thenReturn(messageReceiverMock1);
        when(messageProcessingSequenceMock.getCurrentReceiverArguments()).thenReturn(receiverArguments1);
        when(messageProcessingSequenceMock.next()).thenReturn(true).thenReturn(false);
        messageProcessor.execute();

        try {
            messageProcessor.continueProcess(null);
        } catch (Exception e) {
            fail();
        }

        messageProcessor.continueProcess(null);
    }
}
