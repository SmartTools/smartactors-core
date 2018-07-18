package info.smart_tools.smartactors.endpoint_components_netty.default_transport_providers;

import info.smart_tools.smartactors.endpoint_components_netty.inetty_transport_provider.exceptions.EventLoopGroupCreationException;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollDatagramChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;

/**
 * {@link info.smart_tools.smartactors.endpoint_components_netty.inetty_transport_provider.INettyTransportProvider
 * Transport provider} implementation for Epoll transport.
 */
public class EpollTransportProvider extends BaseTransportProvider {
    /**
     * The constructor.
     */
    public EpollTransportProvider() {
        super(EpollEventLoopGroup.class);

        registerChannelType(ServerSocketChannel.class, EpollServerSocketChannel::new);
        registerChannelType(SocketChannel.class, EpollSocketChannel::new);
        registerChannelType(DatagramChannel.class, EpollDatagramChannel::new);
    }

    @Override
    public EventLoopGroup createEventLoopGroup(final IObject config)
            throws EventLoopGroupCreationException {
        return new EpollEventLoopGroup();
    }
}
