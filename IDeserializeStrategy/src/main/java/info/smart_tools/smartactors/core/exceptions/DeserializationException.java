package info.smart_tools.smartactors.core.exceptions;

/**
 * Exception for error in {@link info.smart_tools.smartactors.core.IDeserializeStrategy}
 */
public class DeserializationException extends Exception {

    /**
     * Default constructor for exception
     */
    public DeserializationException() {
        super();
    }

    /**
     * Constructor with specific error message as argument
     *
     * @param message specific error message
     */
    public DeserializationException(final String message) {
        super(message);
    }


    /**
     * Constructor with specific error message and specific cause as arguments
     *
     * @param message specific error message
     * @param cause   specific cause
     */
    public DeserializationException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with specific cause as argument
     *
     * @param cause specific cause
     */
    public DeserializationException(final Throwable cause) {
        super(cause);
    }
}
