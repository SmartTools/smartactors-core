package info.smart_tools.smartactors.launcher.interfaces.exception.iplugin;

/**
 * Exception thrown when creating plugin
 */
public class PluginCreationException extends Exception {

    /**
     * Constructor with specific error message as argument
     * @param message specific error message
     */
    public PluginCreationException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */

    public PluginCreationException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with specific cause as argument
     * @param cause specific cause
     */
    public PluginCreationException(final Throwable cause) {
        super(cause);
    }
}
