package info.smart_tools.smartactors.message_processing.actor_receiver;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageReceiver;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.AsynchronousOperationException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.MessageReceiveException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Tests for {@link ActorReceiver}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({IOC.class})
@PowerMockIgnore("jdk.internal.reflect.*")
public class ActorReceiverTest {
    private Queue receiverQueueMock;
    private AtomicBoolean receiverFlag;
    private IMessageReceiver childReceiverMock;
    private IMessageProcessor processorMock;

    @Before
    public void setUp()
            throws Exception {
        mockStatic(IOC.class);

        IKey actorReceiverQueueKey = mock(IKey.class);
        IKey actorReceiverBusynessFlagKey = mock(IKey.class);

        receiverQueueMock = mock(Queue.class);
        receiverFlag = new AtomicBoolean(false);
        childReceiverMock = mock(IMessageReceiver.class);
        processorMock = mock(IMessageProcessor.class);

        when(IOC.getKeyForKeyByNameStrategy()).thenReturn(mock(IKey.class));
        when(IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "actor_receiver_queue")).thenReturn(actorReceiverQueueKey);
        when(IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "actor_receiver_busyness_flag")).thenReturn(actorReceiverBusynessFlagKey);

        when(IOC.resolve(actorReceiverQueueKey)).thenReturn(receiverQueueMock);
        when(IOC.resolve(actorReceiverBusynessFlagKey)).thenReturn(receiverFlag);
    }

    @Test(expected = InvalidArgumentException.class)
    public void Should_constructorThrowWhenInitializedWithoutChildReceiver()
            throws Exception {
        assertNotNull(new ActorReceiver(null));
    }

    @Test
    public void Should_enqueueMessageProcessor_When_ActorIsBusy()
            throws Exception {
        receiverFlag.set(true);
        when(receiverQueueMock.isEmpty()).thenReturn(false);

        ActorReceiver actorReceiver = new ActorReceiver(childReceiverMock);

        actorReceiver.receive(processorMock);

        verify(receiverQueueMock).add(same(processorMock));
    }

    @Test
    public void Should_executeReceiverImmediately_When_ActorIsNotBusy()
            throws Exception {
        receiverFlag.set(false);

        doAnswer(invocationOnMock -> {
            assertTrue(receiverFlag.get());
            return null;
        }).when(childReceiverMock).receive(any());

        when(receiverQueueMock.isEmpty()).thenReturn(true);

        ActorReceiver actorReceiver = new ActorReceiver(childReceiverMock);

        actorReceiver.receive(processorMock);

        verify(childReceiverMock).receive(same(processorMock));
    }

    @Test
    public void Should_executeReceiversFromQueue_When_QueueIsNotEmptyAndActorIsNotBusy()
            throws Exception {
        IMessageProcessor[] processorMocks = new IMessageProcessor[]{
                mock(IMessageProcessor.class), mock(IMessageProcessor.class)};

        receiverFlag.set(false);

        doAnswer(invocationOnMock -> {
            assertTrue(receiverFlag.get());
            return null;
        }).when(childReceiverMock).receive(any());

        when(receiverQueueMock.isEmpty())
                .thenAnswer(invocationOnMock -> {
                    assertFalse(receiverFlag.get());
                    return false;
                })
                .thenAnswer(invocationOnMock -> {
                    assertFalse(receiverFlag.get());
                    return true;
                });

        when(receiverQueueMock.poll())
                .thenReturn(processorMocks[0])
                .thenReturn(processorMocks[1])
                .thenReturn(null);

        ActorReceiver actorReceiver = new ActorReceiver(childReceiverMock);

        actorReceiver.receive(processorMock);

        verify(childReceiverMock).receive(processorMock);
        verify(childReceiverMock).receive(processorMocks[0]);
        verify(childReceiverMock).receive(processorMocks[1]);
    }

    @Test
    public void Should_rethrowExceptionAfterCheckingQueue_When_nestedReceiverThrows()
            throws Exception {
        MessageReceiveException exception = mock(MessageReceiveException.class);

        when(receiverQueueMock.isEmpty()).thenReturn(true);
        doAnswer(invocationOnMock -> {
            assertTrue(receiverFlag.get());
            throw exception;
        }).when(childReceiverMock).receive(same(processorMock));

        ActorReceiver actorReceiver = new ActorReceiver(childReceiverMock);

        try {
            actorReceiver.receive(processorMock);
            fail();
        } catch (MessageReceiveException e) {
            assertSame(exception, e.getCause());
        }
    }

    @Test
    public void Should_handleExceptionOccurredWhileCompletingAsynchronousOperationOnDelayedProcessor()
            throws Exception {
        MessageReceiveException messageReceiveExceptionMock = mock(MessageReceiveException.class);
        AsynchronousOperationException asynchronousOperationExceptionMock = mock(AsynchronousOperationException.class);

        when(receiverQueueMock.isEmpty()).thenAnswer(invocationOnMock -> {
            receiverFlag.set(false);
            return false;
        }).thenReturn(true);

        when(receiverQueueMock.poll()).thenReturn(processorMock).thenReturn(null);

        doThrow(messageReceiveExceptionMock).when(childReceiverMock).receive(same(processorMock));
        doThrow(asynchronousOperationExceptionMock).when(processorMock).continueProcess(same(messageReceiveExceptionMock));

        ActorReceiver actorReceiver = new ActorReceiver(childReceiverMock);

        receiverFlag.set(true);

        actorReceiver.receive(processorMock);

        verify(asynchronousOperationExceptionMock).printStackTrace();
        verify(asynchronousOperationExceptionMock).addSuppressed(messageReceiveExceptionMock);
    }


    @Test
    public void Should_propagateDisposeToChildReceiver()
            throws Exception {
        RuntimeException exception = mock(RuntimeException.class);
        ActorReceiver actorReceiver = new ActorReceiver(childReceiverMock);

        actorReceiver.dispose();
        verify(childReceiverMock).dispose();
    }

    @Test
    public void Should_catchChildDisposeException()
            throws Exception {
        RuntimeException exception = mock(RuntimeException.class);
        ActorReceiver actorReceiver = new ActorReceiver(childReceiverMock);

        doThrow(exception).when(childReceiverMock).dispose();

        actorReceiver.dispose();
        verify(childReceiverMock).dispose();
    }
}
