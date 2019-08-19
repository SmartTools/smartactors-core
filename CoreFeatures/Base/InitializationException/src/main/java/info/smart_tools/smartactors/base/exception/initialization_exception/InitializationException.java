package info.smart_tools.smartactors.base.exception.initialization_exception;

/**
 * Checked exception that should be thrown if constructor or init method could not be executed
 */
public class InitializationException extends Exception {

    /**
     * Constructor with a specific error message as the argument
     * @param message the specific error message
     */
    public InitializationException(final String message) {
        super(message);
    }

    /**
     * Constructor with a specific error message and a specific cause as arguments
     * @param message the specific error message
     * @param cause the specific cause
     */
    public InitializationException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with a specific cause as the argument
     * @param cause the specific cause
     */
    public InitializationException(final Throwable cause) {
        super(cause);
    }
}
