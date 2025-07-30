package info.smart_tools.smartactors.base.exception.invalid_state_exception;

/**
 * Exception that should be thrown when method is called when object is in illegal state.
 */
public class InvalidStateException extends Exception {
    /**
     * Constructor with specific error message as argument
     * @param message specific error message
     */
    public InvalidStateException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */

    public InvalidStateException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with specific cause as argument
     * @param cause specific cause
     */
    public InvalidStateException(final Throwable cause) {
        super(cause);
    }
}
