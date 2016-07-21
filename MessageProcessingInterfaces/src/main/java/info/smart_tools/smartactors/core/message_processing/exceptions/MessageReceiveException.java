package info.smart_tools.smartactors.core.message_processing.exceptions;

/**
 * Exception thrown by {@link info.smart_tools.smartactors.core.message_processing.IMessageReceiver}.
 */
public class MessageReceiveException extends Exception {
    /**
     * Default constructor
     */
    public MessageReceiveException() {
    super();
}

    /**
     * Constructor with specific error message as argument
     * @param message specific error message
     */
    public MessageReceiveException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */
    public MessageReceiveException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with specific cause as argument
     * @param cause specific cause
     */
    public MessageReceiveException(final Throwable cause) {
        super(cause);
    }
}
