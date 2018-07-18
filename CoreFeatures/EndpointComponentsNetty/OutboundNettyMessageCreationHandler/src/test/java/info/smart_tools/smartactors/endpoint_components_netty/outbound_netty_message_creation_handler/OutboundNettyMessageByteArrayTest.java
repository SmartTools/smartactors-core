package info.smart_tools.smartactors.endpoint_components_netty.outbound_netty_message_creation_handler;

import info.smart_tools.smartactors.endpoint_interfaces.imessage_byte_array.IOutboundMessageByteArray;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

public class OutboundNettyMessageByteArrayTest {
    @Test public void Should_wrapNettyMessage() throws Exception {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CREATED,
                Unpooled.wrappedBuffer("asd".getBytes()));

        IOutboundMessageByteArray<FullHttpResponse> messageByteArray =
                new OutboundNettyMessageByteArray<>(response);

        assertSame(response, messageByteArray.getMessage());

        messageByteArray.setAsByteBuffer(ByteBuffer.wrap("qwe".getBytes()));

        assertNotSame(response, messageByteArray.getMessage());
        assertEquals("qwe", new String(messageByteArray.getMessage().content().array()));
    }
}
