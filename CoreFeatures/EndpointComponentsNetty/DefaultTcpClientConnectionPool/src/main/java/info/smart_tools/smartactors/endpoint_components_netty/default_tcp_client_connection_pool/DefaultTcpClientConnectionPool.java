package info.smart_tools.smartactors.endpoint_components_netty.default_tcp_client_connection_pool;

import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.interfaces.iaction.IBiFunction;
import info.smart_tools.smartactors.endpoint_components_netty.isocket_connection_pool.ISocketConnectionPool;
import info.smart_tools.smartactors.endpoint_components_netty.isocket_connection_pool.ISocketConnectionPoolObserver;
import info.smart_tools.smartactors.endpoint_components_netty.isocket_connection_pool.exception.SocketConnectionPoolException;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.pool.*;
import io.netty.channel.socket.SocketChannel;

import java.net.InetSocketAddress;

/**
 * Implementation of {@link ISocketConnectionPool} for TCP sockets.
 *
 * <p>
 *  This implementation uses Netty's {@link ChannelPool} and {@link ChannelPoolMap}.
 * </p>
 */
public class DefaultTcpClientConnectionPool implements ISocketConnectionPool<SocketChannel, InetSocketAddress> {
    private final ChannelFactory<? extends SocketChannel> channelFactory;
    private final IAction<SocketChannel> channelSetupCallback;
    private final EventLoopGroup eventLoopGroup;
    private final IBiFunction<Bootstrap, ChannelPoolHandler, ChannelPool> poolFactory;
    private final ISocketConnectionPoolObserver<SocketChannel> observer;

    private final ChannelPoolHandler channelPoolHandler = new ChannelPoolHandler() {
        @Override
        public void channelReleased(final Channel ch) throws Exception {
            observer.onChannelReleased((SocketChannel) ch);
        }

        @Override
        public void channelAcquired(final Channel ch) throws Exception {
            observer.onChannelAcquired((SocketChannel) ch);
        }

        @Override
        public void channelCreated(final Channel ch) throws Exception {
            channelSetupCallback.execute((SocketChannel) ch);
            observer.onChannelCreated((SocketChannel) ch);
        }
    };

    private final ChannelPoolMap<InetSocketAddress, ChannelPool> pools = new AbstractChannelPoolMap<InetSocketAddress, ChannelPool>() {
        @Override
        protected ChannelPool newPool(final InetSocketAddress key) {
            Bootstrap bootstrap = new Bootstrap()
                    .channelFactory(channelFactory)
                    .remoteAddress(key)
                    .group(eventLoopGroup);

            try {
                return poolFactory.execute(bootstrap, channelPoolHandler);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    };

    /**
     * The constructor.
     *
     * @param channelFactory       channel factory that creates channels
     * @param channelSetupCallback action that configures channels (sets up the pipeline)
     * @param eventLoopGroup       event loop group to be used by pooled channels
     * @param poolFactory          factory that creates a {@link ChannelPool} for given bootstrap and {@link ChannelPoolHandler}
     * @param observer             the observer
     */
    public DefaultTcpClientConnectionPool(
            final ChannelFactory<? extends SocketChannel> channelFactory,
            final IAction<SocketChannel> channelSetupCallback,
            final EventLoopGroup eventLoopGroup,
            final IBiFunction<Bootstrap, ChannelPoolHandler, ChannelPool> poolFactory,
            final ISocketConnectionPoolObserver<SocketChannel> observer) {
        this.channelFactory = channelFactory;
        this.channelSetupCallback = channelSetupCallback;
        this.eventLoopGroup = eventLoopGroup;
        this.poolFactory = poolFactory;
        this.observer = observer;
    }

    @Override
    public SocketChannel getChannel(final InetSocketAddress address)
            throws SocketConnectionPoolException {
        try {
            return (SocketChannel) pools.get(address).acquire().sync().getNow();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SocketConnectionPoolException(e);
        } catch (Exception e) {
            throw new SocketConnectionPoolException(e);
        }
    }

    @Override
    public void recycleChannel(final SocketChannel channel)
            throws SocketConnectionPoolException {
        pools.get(channel.remoteAddress()).release(channel);
    }
}
