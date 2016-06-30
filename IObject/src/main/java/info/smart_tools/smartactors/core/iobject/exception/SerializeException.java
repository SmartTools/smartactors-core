package info.smart_tools.smartactors.core.iobject.exception;

/**
 * Exception that occurs when there is an error when serializing instance of {@link info.smart_tools.smartactors.core.iobject.IObject}
 */
public class SerializeException extends Exception {

    /**
     * Default constructor
     */
    public SerializeException() {
        super();
    }

    /**
     * Constructor with specific error message as argument
     * @param message specific error message
     */
    public SerializeException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */
    public SerializeException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with specific cause as argument
     * @param cause specific cause
     */
    public SerializeException(final Throwable cause) {
        super(cause);
    }
}
