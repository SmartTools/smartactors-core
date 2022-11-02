package info.smart_tools.smartactors.base.exception.invalid_argument_exception;

/**
 * Checked exception that should be thrown if constructor received wrong incoming argument
 */
public class InvalidArgumentException extends Exception {

    /**
     * Constructor with specific error message as argument
     * @param message specific error message
     */
    public InvalidArgumentException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */

    public InvalidArgumentException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with specific cause as argument
     * @param cause specific cause
     */
    public InvalidArgumentException(final Throwable cause) {
        super(cause);
    }
}
