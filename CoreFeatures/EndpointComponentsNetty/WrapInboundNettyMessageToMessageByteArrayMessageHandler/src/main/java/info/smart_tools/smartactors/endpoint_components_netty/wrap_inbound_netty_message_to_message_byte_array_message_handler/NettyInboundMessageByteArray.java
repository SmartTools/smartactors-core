package info.smart_tools.smartactors.endpoint_components_netty.wrap_inbound_netty_message_to_message_byte_array_message_handler;

import info.smart_tools.smartactors.endpoint_interfaces.imessage_byte_array.IInboundMessageByteArray;
import io.netty.buffer.ByteBufHolder;

import java.nio.ByteBuffer;

/**
 * {@link IInboundMessageByteArray} that wraps a Netty message implementing {@link ByteBufHolder} interface.
 *
 * @param <T>
 */
public class NettyInboundMessageByteArray<T extends ByteBufHolder> implements IInboundMessageByteArray<T> {
    private final T message;

    /**
     * The constructor.
     *
     * @param message the Netty message
     */
    public NettyInboundMessageByteArray(final T message) {
        this.message = message;
    }

    @Override
    public T getMessage() {
        return message;
    }

    @Override
    public ByteBuffer getAsByteBuffer() {
        return message.content().nioBuffer();
    }
}
