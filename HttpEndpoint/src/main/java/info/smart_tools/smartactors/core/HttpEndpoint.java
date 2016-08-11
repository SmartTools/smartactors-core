package info.smart_tools.smartactors.core;

import info.smart_tools.smartactors.core.endpoint_handler.exceptions.EndpointException;
import info.smart_tools.smartactors.core.http_request_handler.HttpRequestHandler;
import info.smart_tools.smartactors.core.http_server.HttpServer;
import info.smart_tools.smartactors.core.ienvironment_handler.IEnvironmentHandler;
import info.smart_tools.smartactors.core.iscope.IScope;
import info.smart_tools.smartactors.core.message_processing.IReceiverChain;
import io.netty.handler.codec.http.FullHttpRequest;

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
     * @throws EndpointException if there are problems on creating {@link HttpRequestHandler}
     */
    public HttpEndpoint(final int port, final int maxContentLength, final IScope scope,
                        final IEnvironmentHandler handler, final IReceiverChain receiverChain
    ) throws EndpointException {
        super(port, maxContentLength, new EndpointChannelInboundHandler<>(
                new HttpRequestHandler(scope, handler, receiverChain),
                FullHttpRequest.class
        ));
    }
}
