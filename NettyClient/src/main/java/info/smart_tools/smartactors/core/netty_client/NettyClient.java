package info.smart_tools.smartactors.core.netty_client;

import info.smart_tools.smartactors.core.CompletableNettyFuture;
import info.smart_tools.smartactors.core.iclient.IClient;
import info.smart_tools.smartactors.core.iclient.IClientConfig;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.concurrent.Future;

import java.net.URI;
import java.util.concurrent.CompletableFuture;

/**
 * Base class for a client to netty server.
 * TODO:: Check if worker group has been shut down
 * TODO:: Remove constructor without client config
 *
 * @param <TRequest> type of the request which client can send
 */
public abstract class NettyClient<TRequest> implements IClient<TRequest> {
    private static EventLoopGroup workerGroup = new NioEventLoopGroup(1);
    private Channel channel;
    private URI serverUri;
    private Class<? extends Channel> channelClass;
    private ChannelInboundHandler inboundHandler;
    private IClientConfig clientConfig;
    private static final int DEFAULT_CONNECTION_TIMEOUT_MILLIS = 5000;
    private static final int DEFAULT_READ_TIMEOUT_SEC = 5;

    /**
     * Constructor for netty client
     * @param serverUri URI of the server
     * @param channelClass
     * @param inboundHandler
     */
    public NettyClient(final URI serverUri, final Class<? extends Channel> channelClass,
                       final ChannelInboundHandler inboundHandler) {
        this.serverUri = serverUri;
        this.channelClass = channelClass;
        this.inboundHandler = inboundHandler;
    }

    public NettyClient(final Class<? extends Channel> channelClass, final IClientConfig clientConfig) {
        if (clientConfig == null) {
            throw new RuntimeException("Can't create NettyClient: client config is null");
        }
        this.channelClass = channelClass;
        this.clientConfig = clientConfig;
        try {
            this.serverUri = clientConfig.getServerUri();
            this.inboundHandler = (ChannelInboundHandler) clientConfig.getHandler();
        } catch (ReadValueException | ChangeValueException e) {
            throw new RuntimeException("Can't create NettyClient", e);
        }
    }

    @Override
    public CompletableFuture<IClient<TRequest>> start() {
        Bootstrap bootstrap = bootstrapClient();
        NettyClient<TRequest> me = this;
        ChannelFuture future = bootstrap.connect(serverUri.getHost(),
                serverUri.getPort()).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(final ChannelFuture future) throws Exception {
                if (!future.isSuccess()) {
                    //TODO:: send message about connection failed
                    System.out.println("Connection failed!!!");
                    future.cause().printStackTrace();
                }
                me.channel = future.channel();
            }
        });

        return wrapToCompletableFuture(future);
    }

    /**
     * Asynchronously send a request to some server.
     * TODO: can we inherit this method from {@link IExchange} interface?
     *
     * @param request concrete request to send
     * @return a future object which can be used to wait a request transmission
     */
    public CompletableFuture<Void> send(final TRequest request) {
        return CompletableNettyFuture.from(channel.writeAndFlush(request));
    }

    @Override
    public CompletableFuture<IClient<TRequest>> stop() {
        return wrapToCompletableFuture(channel.close())
                .thenCompose(x -> wrapToCompletableFuture(workerGroup.shutdownGracefully()));
    }

    /**
     * Setup a communication channel pipeline.
     * Typically, it will add some decoders for initial bytes received from some kind of Socket.
     *
     * @param pipeline a communication pipeline
     * @return a given pipeline enriched with some input-handling logic.
     */
    protected ChannelPipeline setupPipeline(final ChannelPipeline pipeline) {
        return pipeline;
    }

    /**
     * Get a bootstrap object used to initialize the client (basically, it's just a configuration holder).
     * TODO:: make ChannelInitializer as separate realization
     *
     * @return a bootstrap object
     */
    protected Bootstrap bootstrapClient() {
        return new Bootstrap()
                .group(workerGroup)
                .channel(channelClass)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(final Channel ch) throws Exception {
                        ch.config().setConnectTimeoutMillis(
                                clientConfig != null && clientConfig.getConnectionTimeout() != null ?
                                        clientConfig.getConnectionTimeout() : DEFAULT_CONNECTION_TIMEOUT_MILLIS
                        );
//                        ch.config().setOption(ChannelOption.SO_KEEPALIVE, clientConfig.getKeepAlive());
                        setupPipeline(ch.pipeline())
                                .addLast(new ReadTimeoutHandler(
                                        clientConfig != null && clientConfig.getReadTimeout() != null ?
                                                clientConfig.getReadTimeout() / 1000 : DEFAULT_READ_TIMEOUT_SEC
                                ))
                                .addLast(inboundHandler);
                    }
                });
    }

    private <T> CompletableFuture<IClient<TRequest>> wrapToCompletableFuture(final Future<T> future) {
        final NettyClient<TRequest> me = this;
        return CompletableNettyFuture.from(future).thenApply(x -> me);
    }
}
