package info.smart_tools.smartactors.testing.interfaces.isource.exception;

/**
 * Exception that occurs when there is an error when data source extraction is failed
 */
public class SourceExtractionException extends Exception {

    /**
     * Constructor with specific error message as argument
     * @param message specific error message
     */
    public SourceExtractionException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */
    public SourceExtractionException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with specific cause as argument
     * @param cause specific cause
     */
    public SourceExtractionException(final Throwable cause) {
        super(cause);
    }
}
