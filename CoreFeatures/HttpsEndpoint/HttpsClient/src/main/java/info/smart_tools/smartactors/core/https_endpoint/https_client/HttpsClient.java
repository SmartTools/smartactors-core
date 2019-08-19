package info.smart_tools.smartactors.core.https_endpoint.https_client;

import info.smart_tools.smartactors.endpoint.interfaces.irequest_sender.exception.RequestSenderException;
import info.smart_tools.smartactors.endpoint.interfaces.iresponse_handler.IResponseHandler;
import info.smart_tools.smartactors.endpoint.irequest_maker.IRequestMaker;
import info.smart_tools.smartactors.endpoint.irequest_maker.exception.RequestMakerException;
import info.smart_tools.smartactors.http_endpoint.netty_client.NettyClient;
import info.smart_tools.smartactors.https_endpoint.interfaces.issl_engine_provider.ISslEngineProvider;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLEngine;
import java.net.URI;

/**
 * Client for sending HTTPS requests
 */
public class HttpsClient extends NettyClient<FullHttpRequest> {
    private IResponseHandler inboundHandler;
    private static ISslEngineProvider sslEngineProvider;
    private static final Integer DEFAULT_HTTPS_PORT = 443;


    static {
        try {
            sslEngineProvider = IOC.resolve(Keys.getKeyByName(ISslEngineProvider.class.getCanonicalName()));
        } catch (ResolutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * Constructor
     *
     * @param serverUri      uri of the server for request
     * @param inboundHandler response handler
     * @throws RequestSenderException throw if there is problem with resolving "sslClientContext"
     */
    public HttpsClient(final URI serverUri, final IResponseHandler inboundHandler) throws RequestSenderException {
        super(serverUri, NioSocketChannel.class, inboundHandler);
        this.inboundHandler = inboundHandler;
    }

    @Override
    protected ChannelPipeline setupPipeline(final ChannelPipeline pipeline) {
        SSLEngine clientEngine = sslEngineProvider.getClientContext(
                super.serverUri.getHost(),
                super.serverUri.getPort() == -1 ? DEFAULT_HTTPS_PORT : super.serverUri.getPort()
        );
        SslHandler sslHandler = new SslHandler(clientEngine);
        return super.setupPipeline(pipeline)
                .addLast("ssl", sslHandler)
                .addLast(new HttpClientCodec(), new HttpObjectAggregator(Integer.MAX_VALUE))
                .addLast("handleResponse", new SimpleChannelInboundHandler<FullHttpResponse>() {
                            @Override
                            protected void channelRead0(final ChannelHandlerContext channelHandlerContext, final FullHttpResponse response) throws Exception {
                                inboundHandler.handle(channelHandlerContext, response);
                            }
                        }
                );
    }

    @Override
    public void sendRequest(final IObject request) throws RequestSenderException {
        try {
            IRequestMaker<FullHttpRequest> requestMaker = IOC.resolve(Keys.getKeyByName(IRequestMaker.class.getCanonicalName()));
            FullHttpRequest httpRequest = requestMaker.make(request);
            send(httpRequest);
        } catch (RequestMakerException | ResolutionException e) {
            throw new RequestSenderException(e);
        }
    }
}
