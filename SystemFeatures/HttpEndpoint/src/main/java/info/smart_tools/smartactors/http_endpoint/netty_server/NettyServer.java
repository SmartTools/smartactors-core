package info.smart_tools.smartactors.http_endpoint.netty_server;

import info.smart_tools.smartactors.endpoint.interfaces.iasync_service.IAsyncService;
import info.smart_tools.smartactors.http_endpoint.completable_netty_future.CompletableNettyFuture;
import io.netty.bootstrap.AbstractBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.Future;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Base class for netty based servers. It holds a common initialization/shutdown logic.
 */
public abstract class NettyServer implements IAsyncService<NettyServer> {
    private final int port;
    private Channel channel;

    public NettyServer(final int port) {
        this.port = port;
    }

    @Override
    public CompletableFuture<NettyServer> start() {
        AbstractBootstrap<?, ? extends Channel> bootstrap = bootstrapServer();

        final NettyServer me = this;
        final ChannelFuture channelFuture = bootstrap.bind(port).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(final ChannelFuture channelFuture) throws Exception {
                me.channel = channelFuture.channel();
            }
        });

        return wrapToCompletableFuture(channelFuture);
    }

    @Override
    public CompletableFuture<NettyServer> stop() {
        final NettyServer me = this;
        return CompletableFuture.allOf(
                wrapToCompletableFuture(channel.close()),
                CompletableFuture.allOf(
                        getEventLoopGroups().stream()
                                .map(x -> wrapToCompletableFuture(x.shutdownGracefully()))
                                .toArray(CompletableFuture[]::new)
                )
        ).thenApply(x -> me);
    }

    /**
     * Get a bootstrap object used to initialize the concrete server (basically, it's just a configuration holder).
     * @return a bootstrap object
     */
    protected abstract AbstractBootstrap<?, ? extends Channel> bootstrapServer();

    /**
     * Get server event loop groups in order to shutdown them properly.
     * @return a list of {@link EventLoopGroup} used by the server
     */
    protected List<EventLoopGroup> getEventLoopGroups() {
        return Collections.emptyList();
    }

    private <T> CompletableFuture<NettyServer> wrapToCompletableFuture(final Future<T> future) {
        final NettyServer me = this;
        return CompletableNettyFuture.from(future).thenApply(x -> me);
    }
}

