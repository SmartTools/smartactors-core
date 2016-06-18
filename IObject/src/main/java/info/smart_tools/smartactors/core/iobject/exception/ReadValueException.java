package info.smart_tools.smartactors.core.iobject.exception;

/**
 * Exception that occurs when there is an error when reading the field
 */
public class ReadValueException extends Exception {

    /**
     * Default constructor
     */
    public ReadValueException() {
        super();
    }

    /**
     * Constructor with specific error message as argument
     * @param message specific error message
     */
    public ReadValueException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */
    public ReadValueException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with specific cause as argument
     * @param cause specific cause
     */
    public ReadValueException(final Throwable cause) {
        super(cause);
    }
}
