package info.smart_tools.smartactors.launcher.interfaces.exception.core;

/**
 * Exception thrown on loading launcher core
 */
public class CoreLoaderException extends Exception {

    /**
     * Constructor with specific error message as argument
     * @param message specific error message
     */
    public CoreLoaderException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */

    public CoreLoaderException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with specific cause as argument
     * @param cause specific cause
     */
    public CoreLoaderException(final Throwable cause) {
        super(cause);
    }
}
