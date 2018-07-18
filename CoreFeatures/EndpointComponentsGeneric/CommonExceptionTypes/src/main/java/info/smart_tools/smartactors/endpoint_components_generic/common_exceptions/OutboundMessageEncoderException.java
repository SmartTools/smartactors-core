package info.smart_tools.smartactors.endpoint_components_generic.common_exceptions;

import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.exception.MessageHandlerException;

/**
 * Exception thrown by a handler encoding outbound internal message to external format.
 */
public class OutboundMessageEncoderException extends MessageHandlerException {

    /**
     * Constructs a new exception with {@code null} as its detail message.
     */
    public OutboundMessageEncoderException() {
        super();
    }

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param   message   the detail message.
     */
    public OutboundMessageEncoderException(final String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and
     * cause.
     *
     * @param  message the detail message
     * @param  cause the cause
     */
    public OutboundMessageEncoderException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the specified cause.
     *
     * @param  cause the cause
     */
    public OutboundMessageEncoderException(final Throwable cause) {
        super(cause);
    }
}
