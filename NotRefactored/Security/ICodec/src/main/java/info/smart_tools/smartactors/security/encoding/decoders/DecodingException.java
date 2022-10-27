package info.smart_tools.smartactors.security.encoding.decoders;

/**
 * Exception for errors occurred during decoding message
 */
public class DecodingException extends Exception {

    /**
     * Constructor with specific error message as argument
     * @param message specific error message
     */
    public DecodingException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */
    public DecodingException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
