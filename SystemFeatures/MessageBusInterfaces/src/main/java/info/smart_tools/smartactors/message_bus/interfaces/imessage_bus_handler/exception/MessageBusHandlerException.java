package info.smart_tools.smartactors.message_bus.interfaces.imessage_bus_handler.exception;

/**
 * Exception that occurs when there is an error when handle of message bus handler was failed
 */
public class MessageBusHandlerException extends Exception {

    /**
     * Constructor with specific error message as argument
     * @param message specific error message
     */
    public MessageBusHandlerException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */
    public MessageBusHandlerException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with specific cause as argument
     * @param cause specific cause
     */
    public MessageBusHandlerException(final Throwable cause) {
        super(cause);
    }
}
