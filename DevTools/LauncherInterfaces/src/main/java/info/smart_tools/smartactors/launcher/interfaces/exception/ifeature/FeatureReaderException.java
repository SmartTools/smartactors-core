package info.smart_tools.smartactors.launcher.interfaces.exception.ifeature;

/**
 * Exception thrown by feature reader
 */
public class FeatureReaderException extends Exception {

    /**
     * Constructor with specific error message as argument
     * @param message specific error message
     */
    public FeatureReaderException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */

    public FeatureReaderException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with specific cause as argument
     * @param cause specific cause
     */
    public FeatureReaderException(final Throwable cause) {
        super(cause);
    }
}