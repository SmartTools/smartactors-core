package info.smart_tools.smartactors.iobject.iobject.exception;

/**
 * Exception that occurs when there is an error when deleting the field
 */
public class DeleteValueException extends Exception {

    /**
     * Default constructor
     */
    public DeleteValueException() {
        super();
    }

    /**
     * Constructor with specific error message as argument
     * @param message specific error message
     */
    public DeleteValueException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */
    public DeleteValueException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with specific cause as argument
     * @param cause specific cause
     */
    public DeleteValueException(final Throwable cause) {
        super(cause);
    }
}
