package info.smart_tools.smartactors.endpoint_components_netty.inetty_transport_provider.exceptions;

import io.netty.channel.EventLoopGroup;

/**
 * Exception thrown by {@link info.smart_tools.smartactors.endpoint_components_netty.inetty_transport_provider.INettyTransportProvider#verifyEventLoopGroup(EventLoopGroup)}
 * when given event loop group is not acceptable for the transport implementation.
 */
public class InvalidEventLoopGroupException extends Exception {

    /**
     * Constructs a new exception with {@code null} as its detail message.
     */
    public InvalidEventLoopGroupException() {
        super();
    }

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param   message   the detail message.
     */
    public InvalidEventLoopGroupException(final String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and
     * cause.
     *
     * @param  message the detail message
     * @param  cause the cause
     */
    public InvalidEventLoopGroupException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the specified cause.
     *
     * @param  cause the cause
     */
    public InvalidEventLoopGroupException(final Throwable cause) {
        super(cause);
    }
}
