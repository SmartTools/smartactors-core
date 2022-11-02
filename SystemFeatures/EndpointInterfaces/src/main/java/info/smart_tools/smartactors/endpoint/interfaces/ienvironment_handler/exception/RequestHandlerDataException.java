package info.smart_tools.smartactors.endpoint.interfaces.ienvironment_handler.exception;

import info.smart_tools.smartactors.endpoint.interfaces.ienvironment_handler.IEnvironmentHandler;

/**
 * Exception for {@link IEnvironmentHandler} that should be
 * thrown on invalid request
 */
public class RequestHandlerDataException extends Exception {

    /**
     * Constructor with specific error message
     *
     * @param message specific error message
     */
    public RequestHandlerDataException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     *
     * @param message specific error message
     * @param cause   specific cause
     */
    public RequestHandlerDataException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with specific cause as arguments
     *
     * @param cause specific cause
     */
    public RequestHandlerDataException(final Throwable cause) {
        super(cause);
    }
}
