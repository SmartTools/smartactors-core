package info.smart_tools.smartactors.endpoint_components_netty.isocket_connection_pool.exception;

public class SocketConnectionPoolException extends Exception {
    /**
     * Constructs a new exception with {@code null} as its detail message.
     */
    public SocketConnectionPoolException() {
        super();
    }

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message the detail message.
     */
    public SocketConnectionPoolException(final String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and
     * cause.
     *
     * @param message the detail message * @param cause the cause
     */
    public SocketConnectionPoolException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the specified cause.
     *
     * @param cause the cause
     */
    public SocketConnectionPoolException(final Throwable cause) {
        super(cause);
    }
}
