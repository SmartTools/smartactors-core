package info.smart_tools.smartactors.endpoint_interfaces.imessage_byte_array;

/**
 * Base class for {@link IInboundMessageByteArray} and {@link IOutboundMessageByteArray}.
 *
 * @param <TMessage>
 */
public interface IMessageByteArray<TMessage> {
    /**
     * @return original message
     */
    TMessage getMessage();
}
