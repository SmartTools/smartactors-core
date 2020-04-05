package info.smart_tools.smartactors.http_endpoint.http_client;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.endpoint.interfaces.iclient.IClientConfig;
import info.smart_tools.smartactors.endpoint.interfaces.irequest_sender.exception.RequestSenderException;
import info.smart_tools.smartactors.endpoint.interfaces.iresponse_handler.IResponseHandler;
import info.smart_tools.smartactors.endpoint.irequest_maker.IRequestMaker;
import info.smart_tools.smartactors.endpoint.irequest_maker.exception.RequestMakerException;
import info.smart_tools.smartactors.http_endpoint.netty_client.NettyClient;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;

import java.net.URI;

/**
 * Client for HTTP server
 */
public class HttpClient extends NettyClient<HttpRequest> {
    private IFieldName uriFieldName;
    private IFieldName methodFieldName;
    private IFieldName headersFieldName;
    private IFieldName nameFieldName;
    private IFieldName valueFieldName;
    private IFieldName cookiesFieldName;
    private IFieldName messageMapIdFieldName;
    private IFieldName contentFieldName;
    private String messageMapId;
    private IResponseHandler inboundHandler;

    /**
     * Constructor for http client
     *
     * @param serverUri      URI of the server, that will receive requests
     * @param inboundHandler Channel
     * @throws RequestSenderException if there are exception on resolving IFieldName
     */
    public HttpClient(final URI serverUri, final IResponseHandler inboundHandler) throws RequestSenderException {
        super(serverUri, NioSocketChannel.class, inboundHandler);
        try {
            uriFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "uri");
            methodFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "method");
            headersFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "headers");
            nameFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "name");
            valueFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "value");
            cookiesFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "cookie");
            messageMapIdFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "messageMapId");
            contentFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "content");
        } catch (ResolutionException e) {
            throw new RequestSenderException(e);
        }
        this.inboundHandler = inboundHandler;
    }

    /**
     * Constructor with using client config
     *
     * @param clientConfig Configuration of the current client
     */
    public HttpClient(final IClientConfig clientConfig) {
        super(NioSocketChannel.class, clientConfig);
    }

    //TODO:: set maxContentLength from configuration
    @Override
    protected ChannelPipeline setupPipeline(final ChannelPipeline pipeline) {
        return super.setupPipeline(pipeline)
                .addLast(new HttpClientCodec(), new HttpObjectAggregator(Integer.MAX_VALUE))
                .addLast("handleResponse", new SimpleChannelInboundHandler<FullHttpResponse>() {
                            @Override
                            protected void channelRead0(final ChannelHandlerContext channelHandlerContext, final FullHttpResponse response)
                                    throws Exception {
                                inboundHandler.handle(channelHandlerContext, response);
                            }
                        }
                );
    }

    @Override
    public void sendRequest(final IObject request) throws RequestSenderException {
        try {
            messageMapId = (String) request.getValue(messageMapIdFieldName);
            IRequestMaker<FullHttpRequest> requestMaker = IOC.resolve(Keys.getKeyByName(IRequestMaker.class.getCanonicalName()));
            FullHttpRequest httpRequest = requestMaker.make(request);
            send(httpRequest);
        } catch (RequestMakerException | ReadValueException | ResolutionException | InvalidArgumentException e) {
            throw new RequestSenderException(e);
        }
    }
}
