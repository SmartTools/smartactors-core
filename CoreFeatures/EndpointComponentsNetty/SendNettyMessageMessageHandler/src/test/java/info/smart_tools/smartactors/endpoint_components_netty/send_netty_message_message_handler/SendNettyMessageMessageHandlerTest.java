package info.smart_tools.smartactors.endpoint_components_netty.send_netty_message_message_handler;

import info.smart_tools.smartactors.endpoint_components_generic.default_message_context_implementation.DefaultMessageContextImplementation;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_byte_array.IOutboundMessageByteArray;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IDefaultMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandlerCallback;
import io.netty.channel.Channel;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SendNettyMessageMessageHandlerTest {
    @Test public void Should_sendDestinationMessage() throws Exception {
        IMessageHandlerCallback<IMessageContext> callback = mock(IMessageHandlerCallback.class);
        Channel channel = mock(Channel.class);
        Object message = new Object();
        IOutboundMessageByteArray<Object> messageByteArray = mock(IOutboundMessageByteArray.class);

        when(messageByteArray.getMessage()).thenReturn(message);

        IDefaultMessageContext<Object, IOutboundMessageByteArray<Object>, Channel> context
                = new DefaultMessageContextImplementation().cast(IDefaultMessageContext.class).cast(IDefaultMessageContext.class);

        context.setDstMessage(messageByteArray);
        context.setConnectionContext(channel);

        new SendNettyMessageMessageHandler<Object, IDefaultMessageContext<Object, IOutboundMessageByteArray<Object>, Channel>>()
                .handle(callback, context);

        verifyNoMoreInteractions(callback);

        verify(channel).writeAndFlush(same(message));
    }
}
