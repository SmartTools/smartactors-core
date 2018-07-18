package info.smart_tools.smartactors.endpoint_components_generic.common_exceptions;

import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.exception.MessageHandlerException;

/**
 * Exception thrown by handler decoding inbound external message into internal format.
 */
public class InboundMessageDecoderException extends MessageHandlerException {

    /**
     * Constructs a new exception with {@code null} as its detail message.
     */
    public InboundMessageDecoderException() {
        super();
    }

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param   message   the detail message.
     */
    public InboundMessageDecoderException(final String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and
     * cause.
     *
     * @param  message the detail message
     * @param  cause the cause
     */
    public InboundMessageDecoderException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the specified cause.
     *
     * @param  cause the cause
     */
    public InboundMessageDecoderException(final Throwable cause) {
        super(cause);
    }
}
