package info.smart_tools.smartactors.endpoint_components_netty.default_transport_providers;

import info.smart_tools.smartactors.endpoint_components_netty.inetty_transport_provider.exceptions.EventLoopGroupCreationException;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.oio.OioDatagramChannel;
import io.netty.channel.socket.oio.OioServerSocketChannel;
import io.netty.channel.socket.oio.OioSocketChannel;

/**
 * {@link info.smart_tools.smartactors.endpoint_components_netty.inetty_transport_provider.INettyTransportProvider
 * Transport provider} implementation for OIO (blocking) transport.
 */
public class OioTransportProvider extends BaseTransportProvider {
    /**
     * The constructor.
     */
    public OioTransportProvider() {
        super(OioEventLoopGroup.class);

        registerChannelType(ServerSocketChannel.class, OioServerSocketChannel::new);
        registerChannelType(SocketChannel.class, OioSocketChannel::new);
        registerChannelType(DatagramChannel.class, OioDatagramChannel::new);
    }

    @Override
    public EventLoopGroup createEventLoopGroup(final IObject config)
            throws EventLoopGroupCreationException {
        return new OioEventLoopGroup();
    }
}
