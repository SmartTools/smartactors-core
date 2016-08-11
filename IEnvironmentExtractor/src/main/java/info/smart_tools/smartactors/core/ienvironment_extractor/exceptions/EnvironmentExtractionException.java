package info.smart_tools.smartactors.core.ienvironment_extractor.exceptions;

/**
 * Created by sevenbits on 04.08.16.
 */
public class EnvironmentExtractionException extends Exception {

    /**
     * Constructor with specific error message
     *
     * @param message specific error message
     */
    public EnvironmentExtractionException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     *
     * @param message specific error message
     * @param cause   specific cause
     */
    public EnvironmentExtractionException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with specific cause as arguments
     *
     * @param cause   specific cause
     */
    public EnvironmentExtractionException(final Throwable cause) {
        super(cause);
    }
}
