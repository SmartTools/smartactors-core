package info.smart_tools.smartactors.endpoint_components_netty.inetty_transport_provider.exceptions;

/**
 * Exception thrown by {@link info.smart_tools.smartactors.endpoint_components_netty.inetty_transport_provider.INettyTransportProvider#getChannelFactory(Class)}
 * when channel type is not supported by transport implementation.
 */
public class UnsupportedChannelTypeException extends Exception {

    /**
     * Constructs a new exception with {@code null} as its detail message.
     */
    public UnsupportedChannelTypeException() {
        super();
    }

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param   message   the detail message.
     */
    public UnsupportedChannelTypeException(final String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and
     * cause.
     *
     * @param  message the detail message
     * @param  cause the cause
     */
    public UnsupportedChannelTypeException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the specified cause.
     *
     * @param  cause the cause
     */
    public UnsupportedChannelTypeException(final Throwable cause) {
        super(cause);
    }
}
