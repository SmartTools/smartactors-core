package info.smart_tools.smartactors.https_endpoint.https_server;

import info.smart_tools.smartactors.http_endpoint.tcp_server.TcpServer;
import info.smart_tools.smartactors.http_endpoint.tcp_server.exceptions.ServerInitializationException;
import info.smart_tools.smartactors.https_endpoint.interfaces.issl_engine_provider.ISslEngineProvider;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLEngine;

/**
 * A server which handles HTTPS requests.
 */
public class HttpsServer extends TcpServer {

    private ISslEngineProvider engineProvider;
    private final int maxContentLength;
    private boolean sslEnable = false;

    /**
     * Constructor
     *
     * @param port              port of the tcp server
     * @param requestHandler    channel for tcp server
     * @param maxContentLength  max length of the content
     * @param sslEngineProvider provider of {@link SSLEngine}
     */
    public HttpsServer(final int port, final ChannelInboundHandler requestHandler, final int maxContentLength,
                       final ISslEngineProvider sslEngineProvider) {
        super(port, requestHandler);
        this.maxContentLength = maxContentLength;
        this.engineProvider = sslEngineProvider;
        this.sslEnable = sslEngineProvider.isInitialized();
    }

    /**
     * Method for turning on https
     *
     * @param configuration configuration of endpoint
     * @throws ServerInitializationException if there are problems on resolving {@link ISslEngineProvider}
     */
    public void setSSL(final IObject configuration) throws ServerInitializationException {
        try {
            this.engineProvider = IOC.resolve(Keys.getKeyByName(ISslEngineProvider.class.getCanonicalName()), configuration);
        } catch (ResolutionException e) {
            throw new ServerInitializationException("Failed to resolve ssl context provider", e);
        }
        sslEnable = true;
    }

    @Override
    protected ChannelPipeline setupPipeline(final ChannelPipeline pipeline) {
        if (sslEnable) {
            SSLEngine sslEngine = null;
            sslEngine = engineProvider.getServerContext();
            sslEngine.setUseClientMode(false);
            return pipeline.addLast(
                    new HttpServerCodec(),
                    new HttpObjectAggregator(maxContentLength)
            ).addFirst("ssl", new SslHandler(sslEngine));
        }

        return pipeline.addLast(
                new HttpServerCodec(),
                new HttpObjectAggregator(maxContentLength)
        );
    }
}