package info.smart_tools.smartactors.http_endpoint.http_server;

import info.smart_tools.smartactors.http_endpoint.tcp_server.TcpServer;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * A server which handles HTTP requests.
 * TODO: handle OPTION requests to allow cross-domain requests
 * TODO: handle input/output compression (e.g. gzip, deflate etc.)
 */
public class HttpServer extends TcpServer {
    private final int maxContentLength;

    /**
     * Constructor for HttpServer
     * @param port port of the http server
     * @param maxContentLength max length of the content
     * @param requestHandler channel, that handle request
     */
    public HttpServer(final int port, final int maxContentLength, final ChannelInboundHandler requestHandler) {
        super(port, requestHandler);
        this.maxContentLength = maxContentLength;
    }

    @Override
    protected ChannelPipeline setupPipeline(final ChannelPipeline pipeline) {
        return super.setupPipeline(pipeline).addLast(
                new HttpServerCodec(),
                new HttpObjectAggregator(maxContentLength)
        );
    }
}
