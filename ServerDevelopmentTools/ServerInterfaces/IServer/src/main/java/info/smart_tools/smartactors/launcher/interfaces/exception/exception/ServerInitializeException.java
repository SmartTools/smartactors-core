package info.smart_tools.smartactors.launcher.interfaces.exception.exception;

import info.smart_tools.smartactors.launcher.interfaces.exception.IServer;

/**
 * Exception for error in {@link IServer} initialize method
 */
public class ServerInitializeException extends Exception {

    /**
     * Constructor with specific error message as argument
     * @param message specific error message
     */
    public ServerInitializeException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */

    public ServerInitializeException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with specific cause as argument
     * @param cause specific cause
     */
    public ServerInitializeException(final Throwable cause) {
        super(cause);
    }
}
