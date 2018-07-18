package info.smart_tools.smartactors.endpoint_components_netty.outbound_netty_message_creation_handler;

import info.smart_tools.smartactors.endpoint_interfaces.imessage_byte_array.IOutboundMessageByteArray;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.Unpooled;

import java.nio.ByteBuffer;

/**
 * Implementation of {@link IOutboundMessageByteArray} that wraps a Netty message.
 *
 * <p>
 *  As {@link ByteBufHolder#replace(ByteBuf)} doesn't change existing {@link ByteBufHolder} instance this wrapper has to
 *  copy the Netty message every time so {@link #getMessage()} doesn't return the same object before and after
 *  {@link #setAsByteBuffer(ByteBuffer)} call.
 * </p>
 *
 * @param <T>
 */
public class OutboundNettyMessageByteArray<T extends ByteBufHolder> implements IOutboundMessageByteArray<T> {
    private T message;

    /**
     * The constructor.
     *
     * @param message the Netty message
     */
    public OutboundNettyMessageByteArray(final T message) {
        this.message = message;
    }

    @Override
    public void setAsByteBuffer(final ByteBuffer buffer) {
        T message0 = this.message;

        // Looks like all implementations of ByteBufHolder return object of the same type so this cast should be safe.
        @SuppressWarnings({"unchecked"})
        T message1 = (T) message0.replace(Unpooled.wrappedBuffer(buffer));

        message0.release();

        this.message = message1;
    }

    @Override
    public T getMessage() {
        return message;
    }
}
