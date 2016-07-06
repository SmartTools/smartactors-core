package info.smart_tools.smartactors.core.security.encoding.encoders;

/**
 *
 */
public class EncodingException extends Exception {
    /**
     *
     * @param message
     */
    public EncodingException(String message) {
        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public EncodingException(String message, Throwable cause) {
        super(message, cause);
    }
}
