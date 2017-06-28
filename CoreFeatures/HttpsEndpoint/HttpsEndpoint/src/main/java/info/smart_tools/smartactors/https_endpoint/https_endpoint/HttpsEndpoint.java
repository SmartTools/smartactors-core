package info.smart_tools.smartactors.https_endpoint.https_endpoint;


import info.smart_tools.smartactors.base.iup_counter.IUpCounter;
import info.smart_tools.smartactors.base.iup_counter.exception.UpCounterCallbackExecutionException;
import info.smart_tools.smartactors.endpoint.endpoint_channel_inbound_handler.EndpointChannelInboundHandler;
import info.smart_tools.smartactors.http_endpoint.http_request_handler.HttpRequestHandler;
import info.smart_tools.smartactors.https_endpoint.https_server.HttpsServer;
import info.smart_tools.smartactors.endpoint.interfaces.ienvironment_handler.IEnvironmentHandler;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.message_processing.message_processor.MessageProcessor;
import info.smart_tools.smartactors.scope.iscope.IScope;
import info.smart_tools.smartactors.https_endpoint.interfaces.issl_engine_provider.ISslEngineProvider;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IReceiverChain;
import io.netty.handler.codec.http.FullHttpRequest;

/**
 * Concrete HTTPS endpoint.
 * It setups the underlying server to handle requests in an endpoint way.
 */
public class HttpsEndpoint extends HttpsServer {
    /**
     * Constructor for endpoint
     *
     * @param port               port of the endpoint
     * @param maxContentLength   max length of the content
     * @param scope              scope for endpoint
     * @param handler            handler for environment
     * @param name               name of the endpoint
     * @param receiverChain      chain, that should receive {@link MessageProcessor}
     * @param sslContextProvider provider for ssl context
     * @param upCounter          up-counter to use to subscribe to shutdown request
     * @throws ResolutionException if error occurs resolving ny dependencies
     * @throws UpCounterCallbackExecutionException if error occurs registering shutdown request callbacks
     */
    public HttpsEndpoint(final int port, final int maxContentLength, final IScope scope,
                         final IEnvironmentHandler handler, final String name, final IReceiverChain receiverChain,
                         final ISslEngineProvider sslContextProvider, final IUpCounter upCounter
                         ) throws ResolutionException, UpCounterCallbackExecutionException {
        super(port, new EndpointChannelInboundHandler<>(
                        new HttpRequestHandler(scope, handler, receiverChain, name, upCounter),
                        FullHttpRequest.class),
                maxContentLength, sslContextProvider);
    }
}
