package info.smart_tools.smartactors.endpoint.interfaces.ienvironment_handler.exception;

import info.smart_tools.smartactors.endpoint.interfaces.ienvironment_handler.IEnvironmentHandler;

/**
 * Exception for error in {@link IEnvironmentHandler} method
 */
public class EnvironmentHandleException extends Exception {

    /**
     * Constructor with specific error message as argument
     * @param message specific error message
     */
    public EnvironmentHandleException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */

    public EnvironmentHandleException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with specific cause as argument
     * @param cause specific cause
     */
    public EnvironmentHandleException(final Throwable cause) {
        super(cause);
    }
}
