package info.smart_tools.smartactors.http_endpoint.netty_client;

import info.smart_tools.smartactors.endpoint.interfaces.iclient.IClient;
import info.smart_tools.smartactors.endpoint.interfaces.iclient.IClientConfig;
import info.smart_tools.smartactors.endpoint.interfaces.irequest_sender.IRequestSender;
import info.smart_tools.smartactors.endpoint.interfaces.irequest_sender.exception.RequestSenderException;
import info.smart_tools.smartactors.endpoint.interfaces.iresponse_handler.IResponseHandler;
import info.smart_tools.smartactors.http_endpoint.completable_netty_future.CompletableNettyFuture;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.concurrent.Future;

import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Base class for a client to netty server.
 * TODO:: Check if worker group has been shut down
 * TODO:: Remove constructor without client config
 *
 * @param <TRequest> type of the request which client can send
 */
public abstract class NettyClient<TRequest> implements IClient<TRequest>, IRequestSender {

    private static final int READ_TIME_OUT = 1000;

    private static EventLoopGroup workerGroup = new NioEventLoopGroup(1);
    private Channel channel;
    protected URI serverUri;
    private Class<? extends Channel> channelClass;
    private IResponseHandler inboundHandler;
    private IClientConfig clientConfig;
    private static final int DEFAULT_CONNECTION_TIMEOUT_MILLIS = 5000;
    private static final int DEFAULT_READ_TIMEOUT_SEC = 5;
    private static final int DEFAULT_HTTP_PORT = 80;
    private static final int DEFAULT_HTTPS_PORT = 443;
    private int port;

    /**
     * Constructor for netty client
     *
     * @param serverUri      URI of the server
     * @param channelClass   class of the channel
     * @param inboundHandler response handler
     * @throws RequestSenderException if process of client's start has been completed exceptionally
     */
    public NettyClient(final URI serverUri, final Class<? extends Channel> channelClass, final IResponseHandler inboundHandler)
            throws RequestSenderException {
        this.serverUri = serverUri;
        this.channelClass = channelClass;
        this.inboundHandler = inboundHandler;
        this.port = serverUri.getPort();
        if (port == -1) {
            this.port = serverUri.getScheme().equals("http") ? DEFAULT_HTTP_PORT : DEFAULT_HTTPS_PORT;
        }
        CompletableFuture future = start();
        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RequestSenderException("Error during start client", e);
        }
    }

    public NettyClient(final Class<? extends Channel> channelClass, final IClientConfig clientConfig) {
        if (clientConfig == null) {
            throw new RuntimeException("Can't create NettyClient: client config is null");
        }
        this.channelClass = channelClass;
        this.clientConfig = clientConfig;
        try {
            this.serverUri = clientConfig.getServerUri();
            this.inboundHandler = (IResponseHandler) clientConfig.getHandler();
        } catch (ReadValueException | ChangeValueException e) {
            throw new RuntimeException("Can't create NettyClient", e);
        }
    }

    @Override
    public CompletableFuture<IClient<TRequest>> start() {
        Bootstrap bootstrap = bootstrapClient();
        NettyClient<TRequest> me = this;
        ChannelFuture future = bootstrap.connect(serverUri.getHost(), port).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(final ChannelFuture future) throws Exception {
                if (!future.isSuccess()) {
                    //TODO:: send message about connection failed
                    System.out.println("Connection failed!!!");
                }
                me.channel = future.channel();
            }
        });
        return wrapToCompletableFuture(future);
    }

    /**
     * Asynchronously send a request to some server.
     * TODO: can we inherit this method from IExchange interface?
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
                                                clientConfig.getReadTimeout() / READ_TIME_OUT : DEFAULT_READ_TIMEOUT_SEC
                                ));
                    }
                });
    }

    private <T> CompletableFuture<IClient<TRequest>> wrapToCompletableFuture(final Future<T> future) {
        final NettyClient<TRequest> me = this;
        return CompletableNettyFuture.from(future).thenApply(x -> me);
    }
}
