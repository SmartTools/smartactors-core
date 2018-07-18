package info.smart_tools.smartactors.endpoint_components_netty.inbound_netty_message_reference_count_management_components;

import info.smart_tools.smartactors.endpoint_components_netty.inbound_netty_message_reference_count_management_components.message_extractors.InboundMessageExtractor;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IDefaultMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandlerCallback;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.exception.MessageHandlerException;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.helpers.IBypassMessageHandler;
import io.netty.util.ReferenceCounted;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class ReleaseNettyMessageHandlerTest {
    @Test
    public void Should_decreaseReferenceCountOfMessageWhenNextHandlerExits()
            throws Exception {
        ReferenceCounted referenceCountedMock = mock(ReferenceCounted.class);

        IDefaultMessageContext<ReferenceCounted, Object, Object> messageContextMock
                = mock(IDefaultMessageContext.class);
        when(messageContextMock.getSrcMessage()).thenReturn(referenceCountedMock);

        IMessageHandlerCallback<IDefaultMessageContext<ReferenceCounted, Object, Object>> callbackMock
                = mock(IMessageHandlerCallback.class);
        doAnswer(invocationOnMock -> {
            assertSame(messageContextMock, invocationOnMock.getArgumentAt(0, IMessageContext.class));
            verifyNoMoreInteractions(referenceCountedMock);
            return null;
        }).when(callbackMock).handle(any());

        IBypassMessageHandler<IDefaultMessageContext<ReferenceCounted, Object, Object>> handler
                = new ReleaseNettyMessageHandler<>(new InboundMessageExtractor<>());

        handler.handle(callbackMock, messageContextMock);

        verify(callbackMock).handle(any());
        verify(referenceCountedMock).release();
        verifyNoMoreInteractions(referenceCountedMock);
    }

    @Test
    public void Should_decreaseReferenceCountOfMessageWhenNextHandlerThrows()
            throws Exception {
        ReferenceCounted referenceCountedMock = mock(ReferenceCounted.class);

        IDefaultMessageContext<ReferenceCounted, Object, Object> messageContextMock
                = mock(IDefaultMessageContext.class);
        when(messageContextMock.getSrcMessage()).thenReturn(referenceCountedMock);

        MessageHandlerException exception = new MessageHandlerException();

        IMessageHandlerCallback<IDefaultMessageContext<ReferenceCounted, Object, Object>> callbackMock
                = mock(IMessageHandlerCallback.class);
        doAnswer(invocationOnMock -> {
            assertSame(messageContextMock, invocationOnMock.getArgumentAt(0, IMessageContext.class));
            verifyNoMoreInteractions(referenceCountedMock);
            throw exception;
        }).when(callbackMock).handle(any());

        IBypassMessageHandler<IDefaultMessageContext<ReferenceCounted, Object, Object>> handler
                = new ReleaseNettyMessageHandler<>(new InboundMessageExtractor<>());

        try {
            handler.handle(callbackMock, messageContextMock);
            fail();
        } catch (MessageHandlerException e) {
            assertSame(exception, e);
        }

        verify(callbackMock).handle(any());
        verify(referenceCountedMock).release();
        verifyNoMoreInteractions(referenceCountedMock);
    }
}
