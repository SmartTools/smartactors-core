package info.smart_tools.smartactors.endpoint_interfaces.imessage_byte_array;

import java.nio.ByteBuffer;

/**
 * Interface for a object that wraps a inbound message containing a body that may be represented as a sequence of bytes.
 *
 * @param <TMessage> type of the wrapped message
 */
public interface IInboundMessageByteArray<TMessage> extends IMessageByteArray<TMessage> {
    /**
     * @return message body represented as a byte buffer
     */
    ByteBuffer getAsByteBuffer();
}
