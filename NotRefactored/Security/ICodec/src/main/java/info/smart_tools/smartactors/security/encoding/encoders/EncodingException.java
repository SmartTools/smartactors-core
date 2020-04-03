package info.smart_tools.smartactors.security.encoding.encoders;

/**
 * Exception for errors occurred during encoding message
 */
public class EncodingException extends Exception {
    /**
     * Constructor with specific error message as argument
     * @param message specific error message
     */
    public EncodingException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */
    public EncodingException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
