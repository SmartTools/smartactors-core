package info.smart_tools.smartactors.endpoint_components_netty.read_timeout_connection_pool_observer;

import info.smart_tools.smartactors.endpoint_components_netty.isocket_connection_pool.ISocketConnectionPoolObserver;
import info.smart_tools.smartactors.endpoint_components_netty.isocket_connection_pool.exception.SocketConnectionPoolObserverException;
import io.netty.channel.Channel;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.util.concurrent.TimeUnit;

/**
 * Adds a {@link ReadTimeoutHandler} at beginning of Netty pipeline of channel when it is acquired from pool.
 *
 * @param <TChan>
 */
public class ReadTimeoutConnectionPoolObserver<TChan extends Channel>
        implements ISocketConnectionPoolObserver<TChan> {
    private static final String HANDLER_NAME = "readTimeoutHandler";

    private final long acquiredReadTimeoutMs;

    /**
     * The constructor.
     *
     * @param acquiredReadTimeoutMs timeout in milliseconds
     */
    public ReadTimeoutConnectionPoolObserver(final long acquiredReadTimeoutMs) {
        this.acquiredReadTimeoutMs = acquiredReadTimeoutMs;
    }

    @Override
    public void onChannelCreated(final TChan channel)
            throws SocketConnectionPoolObserverException {
    }

    @Override
    public void onChannelAcquired(final TChan channel)
            throws SocketConnectionPoolObserverException {
        channel.pipeline()
                .addFirst(
                HANDLER_NAME,
                new ReadTimeoutHandler(acquiredReadTimeoutMs, TimeUnit.MILLISECONDS));
    }

    @Override
    public void onChannelReleased(final TChan channel)
            throws SocketConnectionPoolObserverException {
        channel.pipeline()
                .remove(HANDLER_NAME);
    }
}
