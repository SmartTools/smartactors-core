package info.smart_tools.smartactors.http_endpoint.tcp_server.exceptions;

/**
 * Exception for server initialization
 */
public class ServerInitializationException extends Exception {
    /**
     * Constructor with specific error message
     * @param message specific error message
     */
    public ServerInitializationException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */
    public ServerInitializationException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with specific error message
     * @param cause specific cause
     */
    public ServerInitializationException(final Throwable cause) {
        super(cause);
    }
}
