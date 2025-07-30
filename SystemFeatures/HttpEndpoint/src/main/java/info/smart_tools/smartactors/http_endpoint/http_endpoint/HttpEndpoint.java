package info.smart_tools.smartactors.http_endpoint.http_endpoint;

import info.smart_tools.smartactors.base.iup_counter.IUpCounter;
import info.smart_tools.smartactors.base.iup_counter.exception.UpCounterCallbackExecutionException;
import info.smart_tools.smartactors.class_management.interfaces.imodule.IModule;
import info.smart_tools.smartactors.endpoint.endpoint_channel_inbound_handler.EndpointChannelInboundHandler;
import info.smart_tools.smartactors.endpoint.interfaces.ienvironment_handler.IEnvironmentHandler;
import info.smart_tools.smartactors.http_endpoint.http_request_handler.HttpRequestHandler;
import info.smart_tools.smartactors.http_endpoint.http_server.HttpServer;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.scope.iscope.IScope;
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
     * @param module           the id of feature in which context HttpRequestHandler works
     * @param handler          handler for environment
     * @param receiverChainName chain name of chain that should receive {@link io.netty.channel.ChannelOutboundBuffer.MessageProcessor}
     * @param name             name of the endpoint
     * @param upCounter        up-counter to use to register shutdown callbacks
     * @throws ResolutionException if IOC cant resolve smth
     * @throws UpCounterCallbackExecutionException if error occurs setting shutdown callback
     */
    public HttpEndpoint(final int port, final int maxContentLength, final IScope scope,
                        final IModule module, final IEnvironmentHandler handler,
                        final Object receiverChainName, final String name, final IUpCounter upCounter
    ) throws ResolutionException, UpCounterCallbackExecutionException {
        super(port, maxContentLength, new EndpointChannelInboundHandler<>(
                new HttpRequestHandler(scope, module, handler, receiverChainName, name, upCounter),
                FullHttpRequest.class
        ));
    }
}
