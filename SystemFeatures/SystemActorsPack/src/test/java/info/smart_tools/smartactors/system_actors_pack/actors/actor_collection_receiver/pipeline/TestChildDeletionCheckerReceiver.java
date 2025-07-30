package info.smart_tools.smartactors.system_actors_pack.actors.actor_collection_receiver.pipeline;

import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageReceiver;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.MessageReceiveException;
import info.smart_tools.smartactors.system_actors_pack.actors.actor_collection_receiver.interfaces.IChildDeletionCheckStrategy;
import info.smart_tools.smartactors.system_actors_pack.actors.actor_collection_receiver.interfaces.exception.DeletionCheckException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Test for {@link ChildDeletionCheckerReceiver}.
 */
public class TestChildDeletionCheckerReceiver {

    private final IObject envMock = mock(IObject.class);
    private final IObject ctxMock = mock(IObject.class);

    private IMessageReceiver underlyingReceiverMock;
    private IChildDeletionCheckStrategy checkStrategyMock;
    private IAction deletionActionMock;
    private IMessageProcessor messageProcessorMock;

    @Before
    public void setUp() throws Exception {
        underlyingReceiverMock = mock(IMessageReceiver.class);
        checkStrategyMock = mock(IChildDeletionCheckStrategy.class);
        deletionActionMock = mock(IAction.class);
        messageProcessorMock = mock(IMessageProcessor.class);

        when(messageProcessorMock.getEnvironment()).thenReturn(envMock);
    }

    @Test
    public void Should_check()
        throws Exception {
        IMessageReceiver receiver = new ChildDeletionCheckerReceiver(underlyingReceiverMock, ctxMock, checkStrategyMock, deletionActionMock);

        receiver.receive(messageProcessorMock);

        verify(underlyingReceiverMock).receive(same(messageProcessorMock));

        verify(checkStrategyMock).checkDelete(same(ctxMock), same(envMock));

        verifyNoMoreInteractions(deletionActionMock);
    }

    @Test
    public void Should_delete()
        throws Exception {
        IMessageReceiver receiver = new ChildDeletionCheckerReceiver(underlyingReceiverMock, ctxMock, checkStrategyMock, deletionActionMock);

        when(checkStrategyMock.checkDelete(same(ctxMock), same(envMock))).thenReturn(true);
        receiver.receive(messageProcessorMock);

        verify(underlyingReceiverMock).receive(messageProcessorMock);

        verify(checkStrategyMock).checkDelete(same(ctxMock), same(envMock));

        verify(deletionActionMock).execute(same(ctxMock));
    }

    @Test
    public void Should_checkWhenReceiverThrows()
        throws Exception {
        IMessageReceiver receiver = new ChildDeletionCheckerReceiver(underlyingReceiverMock, ctxMock, checkStrategyMock, deletionActionMock);

        doThrow(new MessageReceiveException()).when(underlyingReceiverMock).receive(same(messageProcessorMock));
        doThrow(new DeletionCheckException("")).when(checkStrategyMock).checkDelete(same(ctxMock), same(envMock));

        try {
            receiver.receive(messageProcessorMock);
            fail();
        } catch (MessageReceiveException e) {
            assertEquals(1, e.getCause().getSuppressed().length);
            assertTrue(e.getCause().getSuppressed()[0] instanceof DeletionCheckException);
        }
    }
}
