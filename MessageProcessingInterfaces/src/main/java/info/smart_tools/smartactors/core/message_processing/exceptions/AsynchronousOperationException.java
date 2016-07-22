package info.smart_tools.smartactors.core.message_processing.exceptions;

/**
 * Exception thrown by methods of {@link info.smart_tools.smartactors.core.message_processing.IMessageProcessor} used to
 * perform asynchronous operations if message processor does not expect a call of that method or error occurs during
 * execution of the method.
 */
public class AsynchronousOperationException extends Exception {
    /**
     * Constructor with specific error message as argument
     * @param message specific error message
     */
    public AsynchronousOperationException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */
    public AsynchronousOperationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
