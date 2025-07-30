package info.smart_tools.smartactors.configuration_manager.interfaces.iconfiguration_manager.exceptions;

/**
 * Exception thrown when error occurs during processing (loading, changing, etc) of configuration.
 */
public class ConfigurationProcessingException extends Exception {
    /**
     * Constructor with specific error message as argument
     * @param message specific error message
     */
    public ConfigurationProcessingException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */

    public ConfigurationProcessingException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with specific cause as argument
     * @param cause specific cause
     */
    public ConfigurationProcessingException(final Throwable cause) {
        super(cause);
    }
}
