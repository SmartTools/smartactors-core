package info.smart_tools.smartactors.core.endpoint_handler.exceptions;

/**
 * Exception for endpoints
 */
public class EndpointException extends Exception {

    /**
     * Constructor with specific error message
     *
     * @param message specific error message
     */
    public EndpointException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     *
     * @param message specific error message
     * @param cause   specific cause
     */
    public EndpointException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with specific cause as arguments
     *
     * @param cause specific cause
     */
    public EndpointException(final Throwable cause) {
        super(cause);
    }
}
