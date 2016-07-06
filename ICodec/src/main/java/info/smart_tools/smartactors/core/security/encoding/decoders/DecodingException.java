package info.smart_tools.smartactors.core.security.encoding.decoders;

/**
 *
 */
public class DecodingException extends Exception {
    /**
     *
     * @param message
     */
    public DecodingException(String message) {
        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public DecodingException(String message, Throwable cause) {
        super(message, cause);
    }
}
