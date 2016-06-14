package info.smart_tools.smartactors.core.iobject.exception;

/**
 * Exception that occurs when there is an error when changing the field
 */
public class ChangeValueException extends Exception {

    /**
     * Default constructor
     */
    public ChangeValueException() {
        super();
    }

    /**
     * Constructor with specific error message as argument
     * @param message specific error message
     */
    public ChangeValueException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */
    public ChangeValueException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with specific cause as argument
     * @param cause specific cause
     */
    public ChangeValueException(final Throwable cause) {
        super(cause);
    }
}
