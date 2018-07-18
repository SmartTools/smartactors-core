package info.smart_tools.smartactors.endpoint_components_netty.isocket_connection_pool;

import info.smart_tools.smartactors.endpoint_components_netty.isocket_connection_pool.exception.SocketConnectionPoolObserverException;
import io.netty.channel.Channel;

/**
 * Observer for {@link ISocketConnectionPool}.
 *
 * @param <TChan>
 */
public interface ISocketConnectionPoolObserver<TChan extends Channel> {
    /**
     * Called when a new channel is created within a pool.
     *
     * @param channel the created channel
     * @throws SocketConnectionPoolObserverException if any error occurs
     */
    void onChannelCreated(TChan channel)
            throws SocketConnectionPoolObserverException;

    /**
     * Called when a channel is acquired from pool.
     *
     * @param channel the channel
     * @throws SocketConnectionPoolObserverException if any error occurs
     */
    void onChannelAcquired(TChan channel)
            throws SocketConnectionPoolObserverException;

    /**
     * Called when a channel is released back to pool.
     *
     * @param channel the channel
     * @throws SocketConnectionPoolObserverException if any error occurs
     */
    void onChannelReleased(TChan channel)
            throws SocketConnectionPoolObserverException;
}
