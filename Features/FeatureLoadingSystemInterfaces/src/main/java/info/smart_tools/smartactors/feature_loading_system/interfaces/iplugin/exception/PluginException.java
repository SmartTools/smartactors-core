package info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception;

import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin;

/**
 * Exception for error in {@link IPlugin} methods
 */
public class PluginException extends Exception {

    /**
     * Constructor with specific error message as argument
     * @param message specific error message
     */
    public PluginException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */

    public PluginException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with specific cause as argument
     * @param cause specific cause
     */
    public PluginException(final Throwable cause) {
        super(cause);
    }
}
