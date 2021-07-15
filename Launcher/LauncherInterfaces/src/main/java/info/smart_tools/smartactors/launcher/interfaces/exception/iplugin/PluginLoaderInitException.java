package info.smart_tools.smartactors.launcher.interfaces.exception.iplugin;

/**
 * Exception thrown on plugin loader initialization
 */
public class PluginLoaderInitException extends Exception {

    /**
     * Constructor with specific error message as argument
     * @param message specific error message
     */
    public PluginLoaderInitException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */

    public PluginLoaderInitException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with specific cause as argument
     * @param cause specific cause
     */
    public PluginLoaderInitException(final Throwable cause) {
        super(cause);
    }
}
