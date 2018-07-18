package info.smart_tools.smartactors.endpoint_components_netty.default_transport_providers;

import info.smart_tools.smartactors.endpoint_components_netty.inetty_transport_provider.exceptions.EventLoopGroupCreationException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * {@link info.smart_tools.smartactors.endpoint_components_netty.inetty_transport_provider.INettyTransportProvider
 * Transport provider} implementation for NIO transport.
 */
public class NioTransportProvider extends BaseTransportProvider {
    private final IFieldName threadCountFieldName;

    /**
     * The constructor.
     *
     * @throws ResolutionException if error occurs resolving any dependency
     */
    public NioTransportProvider()
            throws ResolutionException {
        super(NioEventLoopGroup.class);

        registerChannelType(ServerSocketChannel.class, NioServerSocketChannel::new);
        registerChannelType(SocketChannel.class, NioSocketChannel::new);
        registerChannelType(DatagramChannel.class, NioDatagramChannel::new);
        threadCountFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "threads");
    }

    @Override
    public EventLoopGroup createEventLoopGroup(final IObject config)
            throws EventLoopGroupCreationException {
        try {
            Number nThreads = (Number) config.getValue(threadCountFieldName);

            if (null == nThreads) {
                return new NioEventLoopGroup();
            } else {
                return new NioEventLoopGroup(nThreads.intValue());
            }
        } catch (Exception e) {
            throw new EventLoopGroupCreationException("Error creating event loop group for NIO transport.", e);
        }
    }
}
