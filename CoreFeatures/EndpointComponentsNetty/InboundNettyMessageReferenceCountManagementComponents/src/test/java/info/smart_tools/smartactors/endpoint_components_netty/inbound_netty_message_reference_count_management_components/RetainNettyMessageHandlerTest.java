package info.smart_tools.smartactors.endpoint_components_netty.inbound_netty_message_reference_count_management_components;

import info.smart_tools.smartactors.endpoint_components_netty.inbound_netty_message_reference_count_management_components.message_extractors.InboundMessageExtractor;
import info.smart_tools.smartactors.endpoint_components_netty.inbound_netty_message_reference_count_management_components.message_extractors.OutboundMessageExtractor;
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

public class RetainNettyMessageHandlerTest {
    @Test
    public void Should_increaseReferenceCountOfMessage()
            throws Exception {
        ReferenceCounted referenceCountedMock = mock(ReferenceCounted.class);

        IDefaultMessageContext<ReferenceCounted, Object, Object> messageContextMock
                = mock(IDefaultMessageContext.class);
        when(messageContextMock.getSrcMessage()).thenReturn(referenceCountedMock);

        IMessageHandlerCallback<IDefaultMessageContext<ReferenceCounted, Object, Object>> callbackMock
                = mock(IMessageHandlerCallback.class);
        doAnswer(invocationOnMock -> {
            assertSame(messageContextMock, invocationOnMock.getArgumentAt(0, IMessageContext.class));
            verify(referenceCountedMock).retain();
            verifyNoMoreInteractions(referenceCountedMock);
            return null;
        }).when(callbackMock).handle(any());

        IBypassMessageHandler<IDefaultMessageContext<ReferenceCounted, Object, Object>> handler
                = new RetainNettyMessageHandler<>(new InboundMessageExtractor<>());

        handler.handle(callbackMock, messageContextMock);

        verify(callbackMock).handle(any());
        verifyNoMoreInteractions(referenceCountedMock);
    }

    @Test
    public void Should_releaseTheMessageWhenNextHandlerThrows()
            throws Exception {
        ReferenceCounted referenceCountedMock = mock(ReferenceCounted.class);

        IDefaultMessageContext<Object, ReferenceCounted, Object> messageContextMock
                = mock(IDefaultMessageContext.class);
        when(messageContextMock.getDstMessage()).thenReturn(referenceCountedMock);

        MessageHandlerException exception = new MessageHandlerException();

        IMessageHandlerCallback<IDefaultMessageContext<Object, ReferenceCounted, Object>> callbackMock
                = mock(IMessageHandlerCallback.class);
        doAnswer(invocationOnMock -> {
            assertSame(messageContextMock, invocationOnMock.getArgumentAt(0, IMessageContext.class));
            verify(referenceCountedMock).retain();
            verifyNoMoreInteractions(referenceCountedMock);
            throw exception;
        }).when(callbackMock).handle(any());

        IBypassMessageHandler<IDefaultMessageContext<Object, ReferenceCounted, Object>> handler
                = new RetainNettyMessageHandler<>(new OutboundMessageExtractor<>());

        try {
            handler.handle(callbackMock, messageContextMock);
            fail();
        } catch (MessageHandlerException e) {
            assertSame(exception, e);
        }

        verify(referenceCountedMock).release();
    }
}
