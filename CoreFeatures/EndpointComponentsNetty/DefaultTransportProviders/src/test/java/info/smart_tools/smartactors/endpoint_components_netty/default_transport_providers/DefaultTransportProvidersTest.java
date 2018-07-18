package info.smart_tools.smartactors.endpoint_components_netty.default_transport_providers;

import info.smart_tools.smartactors.endpoint_components_netty.inetty_transport_provider.INettyTransportProvider;
import info.smart_tools.smartactors.endpoint_components_netty.inetty_transport_provider.exceptions.InvalidEventLoopGroupException;
import info.smart_tools.smartactors.helpers.trivial_plugins_loading_test_base.TrivialPluginsLoadingTestBase;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import io.netty.channel.epoll.EpollDatagramChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.channel.socket.oio.OioDatagramChannel;
import io.netty.channel.socket.oio.OioServerSocketChannel;
import io.netty.channel.socket.oio.OioSocketChannel;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

public class DefaultTransportProvidersTest extends TrivialPluginsLoadingTestBase {
    @Test public void testOioProvider() throws Exception {
        INettyTransportProvider provider = new OioTransportProvider();

        provider.verifyEventLoopGroup(mock(OioEventLoopGroup.class));

        try {
            provider.verifyEventLoopGroup(mock(NioEventLoopGroup.class));
            fail();
        } catch (InvalidEventLoopGroupException ok) {}

        assertTrue(provider.getChannelFactory(ServerSocketChannel.class).newChannel() instanceof OioServerSocketChannel);
        assertTrue(provider.getChannelFactory(SocketChannel.class).newChannel() instanceof OioSocketChannel);
        assertTrue(provider.getChannelFactory(DatagramChannel.class).newChannel() instanceof OioDatagramChannel);

        assertTrue(provider.createEventLoopGroup(mock(IObject.class)) instanceof OioEventLoopGroup);
    }

    @Test public void testNioProvider() throws Exception {
        INettyTransportProvider provider = new NioTransportProvider();

        provider.verifyEventLoopGroup(mock(NioEventLoopGroup.class));

        try {
            provider.verifyEventLoopGroup(mock(OioEventLoopGroup.class));
            fail();
        } catch (InvalidEventLoopGroupException ok) {}

        assertTrue(provider.getChannelFactory(ServerSocketChannel.class).newChannel() instanceof NioServerSocketChannel);
        assertTrue(provider.getChannelFactory(SocketChannel.class).newChannel() instanceof NioSocketChannel);
        assertTrue(provider.getChannelFactory(DatagramChannel.class).newChannel() instanceof NioDatagramChannel);

        assertTrue(provider.createEventLoopGroup(mock(IObject.class)) instanceof NioEventLoopGroup);
    }

    @Ignore
    @Test public void testEpollProvider() throws Exception {
        if (!System.getProperty("os.name").toLowerCase().trim().startsWith("linux")) {
            return;
        }

        INettyTransportProvider provider = new EpollTransportProvider();

        // Unable to mock EpollEventLoopGroup
        //provider.verifyEventLoopGroup(mock(EpollEventLoopGroup.class));

        try {
            provider.verifyEventLoopGroup(mock(NioEventLoopGroup.class));
            fail();
        } catch (InvalidEventLoopGroupException ok) {}

        // Factories crash
        assertTrue(provider.getChannelFactory(ServerSocketChannel.class).newChannel() instanceof EpollServerSocketChannel);
        assertTrue(provider.getChannelFactory(SocketChannel.class).newChannel() instanceof EpollSocketChannel);
        assertTrue(provider.getChannelFactory(DatagramChannel.class).newChannel() instanceof EpollDatagramChannel);

        assertTrue(provider.createEventLoopGroup(mock(IObject.class)) instanceof EpollEventLoopGroup);
    }
}
