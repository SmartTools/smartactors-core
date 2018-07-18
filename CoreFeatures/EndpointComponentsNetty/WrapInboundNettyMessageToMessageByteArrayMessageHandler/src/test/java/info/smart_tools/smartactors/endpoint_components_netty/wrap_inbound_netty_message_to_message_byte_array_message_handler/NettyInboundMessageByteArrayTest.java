package info.smart_tools.smartactors.endpoint_components_netty.wrap_inbound_netty_message_to_message_byte_array_message_handler;

import info.smart_tools.smartactors.endpoint_interfaces.imessage_byte_array.IInboundMessageByteArray;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_byte_array.IMessageByteArray;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NettyInboundMessageByteArrayTest {
    @Test public void Should_wrapNettyMessage() throws Exception {
        ByteBufHolder message = mock(ByteBufHolder.class);
        ByteBuf byteBuf = mock(ByteBuf.class);
        ByteBuffer nioBuffer = mock(ByteBuffer.class);

        when(message.content()).thenReturn(byteBuf);
        when(byteBuf.nioBuffer()).thenReturn(nioBuffer);

        IInboundMessageByteArray<ByteBufHolder> messageByteArray = new NettyInboundMessageByteArray<>(message);

        assertSame(message, messageByteArray.getMessage());
        assertSame(nioBuffer, messageByteArray.getAsByteBuffer());
    }
}
