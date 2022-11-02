package info.smart_tools.smartactors.base.exception.initialization_exception;

/**
 * Checked exception that should be thrown if constructor or init method could not be executed
 */
public class InitializationException extends Exception {

    /**
     * Constructor with specific error message as argument
     * @param message specific error message
     */
    public InitializationException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */

    public InitializationException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with specific cause as argument
     * @param cause specific cause
     */
    public InitializationException(final Throwable cause) {
        super(cause);
    }
}
