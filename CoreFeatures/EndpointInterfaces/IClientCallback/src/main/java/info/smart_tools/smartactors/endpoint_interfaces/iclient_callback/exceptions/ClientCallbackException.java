package info.smart_tools.smartactors.endpoint_interfaces.iclient_callback.exceptions;

/**
 * Exception thrown by {@link info.smart_tools.smartactors.endpoint_interfaces.iclient_callback.IClientCallback}.
 */
public class ClientCallbackException extends Exception {
    /**
     * Constructs a new exception with {@code null} as its detail message.
     */
    public ClientCallbackException() {
        super();
    }

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message the detail message.
     */
    public ClientCallbackException(final String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and
     * cause.
     *
     * @param message the detail message * @param cause the cause
     */
    public ClientCallbackException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the specified cause.
     *
     * @param cause the cause
     */
    public ClientCallbackException(final Throwable cause) {
        super(cause);
    }
}
