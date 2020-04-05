package info.smart_tools.smartactors.http_endpoint.tcp_server;

import info.smart_tools.smartactors.http_endpoint.netty_server.NettyServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.Arrays;
import java.util.List;

/**
 * A server which handles TCP packets.
 * Server is based on two thread groups:
 *   * master - for accepting and releasing connections
 *   * child - for concrete requests handling
 * TODO: configuration
 * TODO: restrict size of a thread pool for child group?
 */
public class TcpServer extends NettyServer {
    private static final int BACKLOG_SIZE = 128;
    private static final boolean KEEP_ALIVE = true;
    private final NioEventLoopGroup masterGroup = new NioEventLoopGroup(1);
    private final NioEventLoopGroup childGroup = new NioEventLoopGroup();
    private ChannelHandler requestHandler;

    /**
     * Constructor
     * @param port port of the tcp server
     * @param requestHandler channel for tcp server
     */
    public TcpServer(final int port, final ChannelHandler requestHandler) {
        super(port);
        this.requestHandler = requestHandler;
    }

    protected ChannelPipeline setupPipeline(final ChannelPipeline pipeline) {
        return pipeline;
    }

    @Override
    protected ServerBootstrap bootstrapServer() {
        return new ServerBootstrap()
                .group(masterGroup, childGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(final Channel ch) throws Exception {
                        setupPipeline(ch.pipeline()).addLast(requestHandler);
                    }
                })
                .option(ChannelOption.SO_BACKLOG, BACKLOG_SIZE)
                .childOption(ChannelOption.SO_KEEPALIVE, KEEP_ALIVE);
    }

    @Override
    protected List<EventLoopGroup> getEventLoopGroups() {
        return Arrays.asList(masterGroup, childGroup);
    }
}
