package info.smart_tools.smartactors.endpoint_components_netty.isocket_connection_pool;

import info.smart_tools.smartactors.endpoint_components_netty.isocket_connection_pool.exception.SocketConnectionPoolException;
import io.netty.channel.Channel;

import java.net.SocketAddress;

/**
 * Pool of client connections.
 *
 * @param <TChan> type of channel
 * @param <TAddr> type of address
 */
public interface ISocketConnectionPool<TChan extends Channel, TAddr extends SocketAddress> {
    /**
     * Get a channel connected to given address from pool or create it.
     *
     * @param address remote address
     * @return the channel
     * @throws SocketConnectionPoolException if any error occurs
     */
    TChan getChannel(TAddr address) throws SocketConnectionPoolException;

    /**
     * This method should be called when the channel taken from this pool is no longer needed.
     *
     * @param channel channel
     * @throws SocketConnectionPoolException if any error occurs
     */
    void recycleChannel(TChan channel) throws SocketConnectionPoolException;
}
