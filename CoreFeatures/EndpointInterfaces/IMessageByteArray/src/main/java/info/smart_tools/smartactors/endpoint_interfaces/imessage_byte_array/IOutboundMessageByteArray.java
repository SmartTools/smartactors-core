package info.smart_tools.smartactors.endpoint_interfaces.imessage_byte_array;

import java.nio.ByteBuffer;

/**
 * Interface for a object that wraps a outbound message which body can be set from byte sequence.
 *
 * @param <TMessage> type of the wrapped message
 */
public interface IOutboundMessageByteArray<TMessage> extends IMessageByteArray<TMessage> {
    /**
     * @param buffer new message body as a byte buffer
     */
    void setAsByteBuffer(ByteBuffer buffer);
}
