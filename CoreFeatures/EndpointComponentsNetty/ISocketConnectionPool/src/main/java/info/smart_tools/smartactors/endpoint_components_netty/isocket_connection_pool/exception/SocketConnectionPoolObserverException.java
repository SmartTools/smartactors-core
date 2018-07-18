package info.smart_tools.smartactors.endpoint_components_netty.isocket_connection_pool.exception;

/**
 * Exception thrown by {@link info.smart_tools.smartactors.endpoint_components_netty.isocket_connection_pool.ISocketConnectionPoolObserver}.
 */
public class SocketConnectionPoolObserverException extends Exception {
    /**
     * Constructs a new exception with {@code null} as its detail message.
     */
    public SocketConnectionPoolObserverException() {
        super();
    }

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message the detail message.
     */
    public SocketConnectionPoolObserverException(final String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and
     * cause.
     *
     * @param message the detail message * @param cause the cause
     */
    public SocketConnectionPoolObserverException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the specified cause.
     *
     * @param cause the cause
     */
    public SocketConnectionPoolObserverException(final Throwable cause) {
        super(cause);
    }
}
