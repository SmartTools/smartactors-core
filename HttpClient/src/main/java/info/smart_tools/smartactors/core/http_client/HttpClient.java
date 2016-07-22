package info.smart_tools.smartactors.core.http_client;

import info.smart_tools.smartactors.core.iclient.IClientConfig;
import info.smart_tools.smartactors.core.netty_client.NettyClient;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequest;

import java.net.URI;

/**
 * Client for HTTP server
 */
public class HttpClient extends NettyClient<HttpRequest> {
    /**
     * Constructor for http client
     * @param serverUri URI of the server, that will receive requests
     * @param inboundHandler Channel
     */
    public HttpClient(final URI serverUri, final ChannelInboundHandler inboundHandler) {
        super(serverUri, NioSocketChannel.class, inboundHandler);
    }

    /**
     * Constructor with using client config
     * @param clientConfig Configuration of the current client
     */
    public HttpClient(final IClientConfig clientConfig) {
        super(NioSocketChannel.class, clientConfig);
    }

    //TODO:: set maxContentLength from configuration
    @Override
    protected ChannelPipeline setupPipeline(final ChannelPipeline pipeline) {
        return super.setupPipeline(pipeline).addLast(new HttpClientCodec(), new HttpObjectAggregator(4096));
    }
}
