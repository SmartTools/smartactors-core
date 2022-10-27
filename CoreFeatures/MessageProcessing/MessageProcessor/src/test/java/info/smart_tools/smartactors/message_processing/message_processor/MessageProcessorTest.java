package info.smart_tools.smartactors.message_processing.message_processor;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.iup_counter.IUpCounter;
import info.smart_tools.smartactors.iobject.field_name.FieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.message_processing_interfaces.imessage.IMessage;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessingSequence;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageReceiver;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.Signal;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.AsynchronousOperationException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.MessageReceiveException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.NoExceptionHandleChainException;
import info.smart_tools.smartactors.shutdown.ishutdown_aware_task.IShutdownAwareTask;
import info.smart_tools.smartactors.task.interfaces.iqueue.IQueue;
import info.smart_tools.smartactors.task.interfaces.itask.ITask;
import info.smart_tools.smartactors.task.interfaces.itask.exception.TaskExecutionException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

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
    private final Signal signal = new Signal("test") { };
    private final Signal shutdownSignal = new Signal("shutdown") { };

    private IQueue<ITask> taskQueueMock;
    private IMessageProcessingSequence messageProcessingSequenceMock;
    private IMessage messageMock;
    private IObject contextMock;
    private IObject responseMock;
    private IObject environmentMock;
    private IObject configurationMock;
    private ITask finalTaskMock;
    private IUpCounter upCounterMock;

    private final IKey KEY_FOR_KEY_STORAGE = mock(IKey.class);
    private final IKey KEY_FOR_NEW_IOBJECT = mock(IKey.class);
    private final IKey KEY_FOR_FIELD_NAME = mock(IKey.class);
    private final IKey KEY_FOR_FINAL_TASK = mock(IKey.class);
    private final IKey KEY_FOR_TEST_SIGNAL = mock(IKey.class);
    private final IKey KEY_FOR_SHUTDOWN_SIGNAL = mock(IKey.class);
    private final IKey KEY_INVALID_KEY = mock(IKey.class);
    private final IKey KEY_FOR_UP_COUNTER = mock(IKey.class);

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
        finalTaskMock = mock(ITask.class);
        upCounterMock = mock(IUpCounter.class);

        mockStatic(IOC.class);

        when(IOC.getKeyForKeyByNameStrategy()).thenReturn(KEY_FOR_KEY_STORAGE);
        when(IOC.resolve(KEY_FOR_KEY_STORAGE, "final task")).thenReturn(KEY_FOR_FINAL_TASK);
        when(IOC.resolve(KEY_FOR_KEY_STORAGE, "root upcounter")).thenReturn(KEY_FOR_UP_COUNTER);
        when(IOC.resolve(KEY_FOR_KEY_STORAGE, "test signal")).thenReturn(KEY_FOR_TEST_SIGNAL);
        when(IOC.resolve(KEY_FOR_KEY_STORAGE, "shutdown signal")).thenReturn(KEY_FOR_SHUTDOWN_SIGNAL);
        when(IOC.resolve(KEY_FOR_KEY_STORAGE, "invalid key")).thenReturn(KEY_INVALID_KEY);
        when(IOC.resolve(KEY_FOR_KEY_STORAGE, "info.smart_tools.smartactors.iobject.iobject.IObject")).thenReturn(KEY_FOR_NEW_IOBJECT);
        when(IOC.resolve(KEY_FOR_NEW_IOBJECT))
                .thenReturn(environmentMock);
        when(IOC.resolve(KEY_FOR_KEY_STORAGE, "info.smart_tools.smartactors.iobject.ifield_name.IFieldName")).thenReturn(KEY_FOR_FIELD_NAME);
        when(IOC.resolve(same(KEY_FOR_FIELD_NAME), any()))
                .thenAnswer(invocationOnMock -> new FieldName((String) invocationOnMock.getArguments()[1]));
        when(IOC.resolve(same(KEY_FOR_FINAL_TASK), any())).thenReturn(this.finalTaskMock);
        when(IOC.resolve(same(KEY_FOR_UP_COUNTER))).thenReturn(upCounterMock);
        when(IOC.resolve(same(KEY_FOR_TEST_SIGNAL))).thenReturn(signal);
        when(IOC.resolve(same(KEY_FOR_SHUTDOWN_SIGNAL))).thenReturn(shutdownSignal);
        when(IOC.resolve(same(KEY_INVALID_KEY))).thenThrow(ResolutionException.class);
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
        verify(environmentMock).setValue(eq(IOC.resolve(KEY_FOR_FIELD_NAME, "processor")), same(messageProcessor));

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
        verify(this.taskQueueMock, times(1)).put(this.finalTaskMock);
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

    @Test(expected = InvalidArgumentException.class)
    public void Should_throwWhenUpdatedEnvironmentIsNull()
            throws Exception {
        new MessageProcessor(taskQueueMock, messageProcessingSequenceMock, configurationMock).pushEnvironment(null);
    }

    @Test
    public void Should_setEnvironmentAndResetItOnNextStep()
            throws Exception {
        MessageProcessor messageProcessor = new MessageProcessor(taskQueueMock, messageProcessingSequenceMock, configurationMock);

        IObject nEnv = mock(IObject.class);

        messageProcessor.process(messageMock, contextMock);
        messageProcessor.pushEnvironment(nEnv);

        assertSame(nEnv, messageProcessor.getEnvironment());

        messageProcessor.execute();

        assertSame(environmentMock, messageProcessor.getEnvironment());
    }

    @Test
    public void Should_setEnvironmentAndResetItOnResetEnvironmentCall()
            throws Exception {
        MessageProcessor messageProcessor = new MessageProcessor(taskQueueMock, messageProcessingSequenceMock, configurationMock);

        IObject nEnv = mock(IObject.class);

        messageProcessor.process(messageMock, contextMock);
        messageProcessor.pushEnvironment(nEnv);

        assertSame(nEnv, messageProcessor.getEnvironment());

        messageProcessor.resetEnvironment();

        assertSame(environmentMock, messageProcessor.getEnvironment());
    }

    @Test
    public void Should_handleSignals()
            throws Exception {
        MessageProcessor messageProcessor = new MessageProcessor(taskQueueMock, messageProcessingSequenceMock, configurationMock);

        messageProcessor.signal("test signal");

        messageProcessor.process(messageMock, contextMock);
        messageProcessor.execute();

        verify(messageProcessingSequenceMock).catchException(same(signal), same(contextMock));
    }

    @Test(expected = InvalidArgumentException.class)
    public void Should_throwWhenSignalNameIsNull()
            throws Exception {
        MessageProcessor messageProcessor = new MessageProcessor(taskQueueMock, messageProcessingSequenceMock, configurationMock);

        messageProcessor.signal(null);
    }

    @Test(expected = InvalidArgumentException.class)
    public void Should_throwWhenSignalNameIsInvalid()
            throws Exception {
        MessageProcessor messageProcessor = new MessageProcessor(taskQueueMock, messageProcessingSequenceMock, configurationMock);

        messageProcessor.signal("invalid key");
    }

    @Test(expected = InvalidArgumentException.class)
    public void Should_throwWhenSignalIsNotSignal()
            throws Exception {
        MessageProcessor messageProcessor = new MessageProcessor(taskQueueMock, messageProcessingSequenceMock, configurationMock);

        messageProcessor.signal("info.smart_tools.smartactors.iobject.iobject.IObject");
    }

    @Test
    public void Should_endSequenceSilentlyWhenThereIsNoExceptionalChainForSignal()
            throws Exception {
        doThrow(NoExceptionHandleChainException.class).when(messageProcessingSequenceMock).catchException(same(signal), any());

        MessageProcessor messageProcessor = new MessageProcessor(taskQueueMock, messageProcessingSequenceMock, configurationMock);

        messageProcessor.signal("test signal");
        messageProcessor.execute();

        verify(messageProcessingSequenceMock).end();
    }

    @Test
    public void Should_interactWithUpCounter()
            throws Exception {
        MessageProcessor messageProcessor = new MessageProcessor(taskQueueMock, messageProcessingSequenceMock, configurationMock);

        messageProcessor.process(messageMock, contextMock);

        verify(upCounterMock, times(1)).up();
        verifyNoMoreInteractions(upCounterMock);

        ArgumentCaptor<List> finalActionListCaptor = ArgumentCaptor.forClass(List.class);

        verify(contextMock).setValue(eq(new FieldName("finalActions")), finalActionListCaptor.capture());

        assertEquals(1, finalActionListCaptor.getValue().size());

        ((IAction)finalActionListCaptor.getValue().get(0)).execute(environmentMock);

        verify(upCounterMock, times(1)).down();
    }

    @Test
    public void Should_notOverwriteExistFinalActionsList()
            throws Exception {
        IAction<IObject> actionStub = env -> {};
        List<IAction<IObject>> list = new ArrayList<>();
        list.add(actionStub);

        when(contextMock.getValue(eq(new FieldName("finalActions")))).thenReturn(list);

        MessageProcessor messageProcessor = new MessageProcessor(taskQueueMock, messageProcessingSequenceMock, configurationMock);

        messageProcessor.process(messageMock, contextMock);

        verify(contextMock, times(0)).setValue(eq(new FieldName("finalActions")), any());

        assertSame(actionStub, list.get(0));
        assertEquals(2, list.size());
    }

    @Test
    public void Should_haveAssociatedShutdownAwareTask()
            throws Exception {
        MessageProcessor messageProcessor = new MessageProcessor(taskQueueMock, messageProcessingSequenceMock, configurationMock);

        IShutdownAwareTask shutdownAwareTask = messageProcessor.getAs(IShutdownAwareTask.class);

        assertNotNull(shutdownAwareTask);
    }

    @Test(expected = InvalidArgumentException.class)
    public void Should_throwWhenAssociatedObjectOfUnknownClassRequired()
            throws Exception {
        new MessageProcessor(taskQueueMock, messageProcessingSequenceMock, configurationMock).getAs(IObject.class);
    }

    @Test
    public void Should_emitShutdownSignalWhenNotifiedOnShutdownButExactlyOnce()
            throws Exception {
        MessageProcessor messageProcessor = new MessageProcessor(taskQueueMock, messageProcessingSequenceMock, configurationMock);

        messageProcessor.getAs(IShutdownAwareTask.class).notifyShuttingDown();
        messageProcessor.execute();
        messageProcessor.getAs(IShutdownAwareTask.class).notifyShuttingDown();
        messageProcessor.execute();

        verify(messageProcessingSequenceMock, times(1)).catchException(same(shutdownSignal), any());
    }
}
