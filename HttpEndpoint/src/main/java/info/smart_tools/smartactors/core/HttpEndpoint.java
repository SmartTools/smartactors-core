package info.smart_tools.smartactors.core;

import info.smart_tools.smartactors.core.http_request_handler.HttpRequestHandler;
import info.smart_tools.smartactors.core.http_server.HttpServer;
import info.smart_tools.smartactors.core.ienvironment_handler.IEnvironmentHandler;
import info.smart_tools.smartactors.core.iscope.IScope;
import info.smart_tools.smartactors.core.message_processing.IReceiverChain;
import io.netty.handler.codec.http.FullHttpRequest;

import java.util.Map;

/**
 * Endpoint for http connection
 */
public class HttpEndpoint extends HttpServer {
    /**
     * Constructor for endpoint
     *
     * @param port             port of the endpoint
     * @param maxContentLength max length of the content
     * @param scope            scope for endpoint
     * @param handler          handler for environment
     * @param receiverChain    chain, that should receive {@link info.smart_tools.smartactors.core.message_processor.MessageProcessor}
     * @param strategies       map of the deserialize strategies, where key is content-type
     *                         and value is strategy for that content type
     */
    public HttpEndpoint(final int port, final int maxContentLength, final IScope scope,
                        final IEnvironmentHandler handler, final IReceiverChain receiverChain,
                        final Map<String, IDeserializeStrategy> strategies
    ) {
        super(port, maxContentLength, new EndpointChannelInboundHandler<>(
                new HttpRequestHandler(scope, handler, receiverChain, strategies),
                FullHttpRequest.class
        ));
    }
}
