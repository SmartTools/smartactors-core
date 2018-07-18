package info.smart_tools.smartactors.endpoint_interfaces.ioutbound_connection_channel.exceptions;

/**
 * Exception thrown by {@link info.smart_tools.smartactors.endpoint_interfaces.ioutbound_connection_channel.IOutboundConnectionChannel}.
 */
public class OutboundMessageSendException extends Exception {

    /**
     * Constructs a new exception with {@code null} as its detail message.
     */
    public OutboundMessageSendException() {
        super();
    }

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param   message   the detail message.
     */
    public OutboundMessageSendException(final String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and
     * cause.
     *
     * @param  message the detail message
     * @param  cause the cause
     */
    public OutboundMessageSendException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the specified cause.
     *
     * @param  cause the cause
     */
    public OutboundMessageSendException(final Throwable cause) {
        super(cause);
    }
}
