package info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin_loader.exception;

import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin_loader.IPluginLoader;

/**
 * Exception for error in {@link IPluginLoader} methods
 */
public class PluginLoaderException extends Exception {

    /**
     * Constructor with specific error message as argument
     * @param message specific error message
     */
    public PluginLoaderException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */

    public PluginLoaderException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with specific cause as argument
     * @param cause specific cause
     */
    public PluginLoaderException(final Throwable cause) {
        super(cause);
    }
}
