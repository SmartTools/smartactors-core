package info.smart_tools.smartactors.endpoint_interfaces.ioutbound_connection_channel.exceptions;

/**
 * Exception thrown by {@link info.smart_tools.smartactors.endpoint_interfaces.ioutbound_connection_channel.IOutboundConnectionChannelListener}.
 */
public class OutboundChannelListenerException extends Exception {

    /**
     * Constructs a new exception with {@code null} as its detail message.
     */
    public OutboundChannelListenerException() {
        super();
    }

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param   message   the detail message.
     */
    public OutboundChannelListenerException(final String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and
     * cause.
     *
     * @param  message the detail message
     * @param  cause the cause
     */
    public OutboundChannelListenerException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the specified cause.
     *
     * @param  cause the cause
     */
    public OutboundChannelListenerException(final Throwable cause) {
        super(cause);
    }
}
