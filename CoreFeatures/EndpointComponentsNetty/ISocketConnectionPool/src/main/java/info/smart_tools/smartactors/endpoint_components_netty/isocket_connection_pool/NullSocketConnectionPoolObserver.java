package info.smart_tools.smartactors.endpoint_components_netty.isocket_connection_pool;

import info.smart_tools.smartactors.endpoint_components_netty.isocket_connection_pool.exception.SocketConnectionPoolObserverException;
import io.netty.channel.Channel;

public enum NullSocketConnectionPoolObserver implements ISocketConnectionPoolObserver {
    INSTANCE;

    @Override
    public void onChannelCreated(final Channel channel)
            throws SocketConnectionPoolObserverException {
    }

    @Override
    public void onChannelAcquired(final Channel channel)
            throws SocketConnectionPoolObserverException {
    }

    @Override
    public void onChannelReleased(final Channel channel)
            throws SocketConnectionPoolObserverException {
    }

    /**
     * Get a instance of {@link NullSocketConnectionPoolObserver}.
     *
     * @param <T> channel type
     * @return the null-{@link ISocketConnectionPoolObserver}
     */
    @SuppressWarnings({"unchecked"})
    public static  <T extends Channel> ISocketConnectionPoolObserver<T> get() {
        return INSTANCE;
    }
}
