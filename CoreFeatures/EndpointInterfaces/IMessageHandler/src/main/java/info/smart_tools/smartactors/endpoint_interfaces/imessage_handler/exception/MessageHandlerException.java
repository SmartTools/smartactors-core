package info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.exception;

/**
 * Exception thrown by {@link info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandler} and
 * {@link info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandlerCallback} when any error
 * occurs handling a message.
 */
public class MessageHandlerException extends Exception {

    /**
     * Constructs a new exception with {@code null} as its detail message.
     */
    public MessageHandlerException() {
        super();
    }

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param   message   the detail message.
     */
    public MessageHandlerException(final String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and
     * cause.
     *
     * @param  message the detail message
     * @param  cause the cause
     */
    public MessageHandlerException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the specified cause.
     *
     * @param  cause the cause
     */
    public MessageHandlerException(final Throwable cause) {
        super(cause);
    }
}
