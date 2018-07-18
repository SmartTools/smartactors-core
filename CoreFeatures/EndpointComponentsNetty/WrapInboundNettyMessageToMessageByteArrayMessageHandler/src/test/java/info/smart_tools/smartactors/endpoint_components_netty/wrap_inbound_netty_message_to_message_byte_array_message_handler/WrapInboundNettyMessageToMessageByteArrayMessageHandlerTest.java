package info.smart_tools.smartactors.endpoint_components_netty.wrap_inbound_netty_message_to_message_byte_array_message_handler;

import info.smart_tools.smartactors.endpoint_components_generic.default_message_context_implementation.DefaultMessageContextImplementation;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_byte_array.IInboundMessageByteArray;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IDefaultMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandlerCallback;
import io.netty.buffer.ByteBufHolder;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class WrapInboundNettyMessageToMessageByteArrayMessageHandlerTest {
    @Test public void Should_wrapSourceMessage() throws Exception {
        ByteBufHolder message = mock(ByteBufHolder.class);
        IMessageHandlerCallback<IDefaultMessageContext<IInboundMessageByteArray<ByteBufHolder>, Object, Object>>
                callback = mock(IMessageHandlerCallback.class);
        IDefaultMessageContext<ByteBufHolder, Object, Object>
                messageContext = new DefaultMessageContextImplementation().cast(IDefaultMessageContext.class)
                .<IDefaultMessageContext<ByteBufHolder, Object, Object>>cast(IDefaultMessageContext.class);

        messageContext.setSrcMessage(message);

        doAnswer(invocationOnMock -> {
            assertSame(messageContext, invocationOnMock.getArgumentAt(0, IDefaultMessageContext.class));
            IDefaultMessageContext ctx = invocationOnMock.getArgumentAt(0, IDefaultMessageContext.class);
            assertNotNull(ctx.getSrcMessage());
            assertTrue(ctx.getSrcMessage() instanceof IInboundMessageByteArray);
            assertEquals(message, ((IInboundMessageByteArray) ctx.getSrcMessage()).getMessage());

            return null;
        }).when(callback).handle(any());

        new WrapInboundNettyMessageToMessageByteArrayMessageHandler<>()
                .handle(callback, messageContext);

        verify(callback).handle(same(messageContext.cast(IDefaultMessageContext.class)));
    }
}
