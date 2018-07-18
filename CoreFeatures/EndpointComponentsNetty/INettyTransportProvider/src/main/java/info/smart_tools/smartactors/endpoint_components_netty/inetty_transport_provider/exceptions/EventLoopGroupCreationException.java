package info.smart_tools.smartactors.endpoint_components_netty.inetty_transport_provider.exceptions;

import info.smart_tools.smartactors.iobject.iobject.IObject;

/**
 * Exception thrown by {@link info.smart_tools.smartactors.endpoint_components_netty.inetty_transport_provider.INettyTransportProvider#createEventLoopGroup(IObject)}
 * when error occurs creating a event loop group.
 */
public class EventLoopGroupCreationException extends Exception {

    /**
     * Constructs a new exception with {@code null} as its detail message.
     */
    public EventLoopGroupCreationException() {
        super();
    }

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param   message   the detail message.
     */
    public EventLoopGroupCreationException(final String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and
     * cause.
     *
     * @param  message the detail message
     * @param  cause the cause
     */
    public EventLoopGroupCreationException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the specified cause.
     *
     * @param  cause the cause
     */
    public EventLoopGroupCreationException(final Throwable cause) {
        super(cause);
    }
}
