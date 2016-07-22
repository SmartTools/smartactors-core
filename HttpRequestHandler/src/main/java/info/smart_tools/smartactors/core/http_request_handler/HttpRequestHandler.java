package info.smart_tools.smartactors.core.http_request_handler;


import info.smart_tools.smartactors.core.IDeserializeStrategy;
import info.smart_tools.smartactors.core.channel_handler_netty.ChannelHandlerNetty;
import info.smart_tools.smartactors.core.endpoint_handler.EndpointHandler;
import info.smart_tools.smartactors.core.ichannel_handler.IChannelHandler;
import info.smart_tools.smartactors.core.ienvironment_handler.IEnvironmentHandler;
import info.smart_tools.smartactors.core.ifield_name.IFieldName;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.iscope.IScope;
import info.smart_tools.smartactors.core.message_processing.IReceiverChain;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;

import java.util.ArrayList;
import java.util.Map;

/**
 * Endpoint handler for HTTP requests.
 */
public class HttpRequestHandler extends EndpointHandler<ChannelHandlerContext, FullHttpRequest> {
    private final Map<String, IDeserializeStrategy> deserializeStrategies;

    /**
     * Constructor for HttpRequestHandler
     *
     * @param scope                 scope for HttpRequestHandler
     * @param environmentHandler    handler for environment
     * @param receiver              chain, that should receive message
     * @param deserializeStrategies map of the deserialize strategies, where key is content-type
     *                              and value is strategy for that content type
     */
    public HttpRequestHandler(
            final IScope scope, final IEnvironmentHandler environmentHandler, final IReceiverChain receiver,
            final Map<String, IDeserializeStrategy> deserializeStrategies) {
        super(receiver, environmentHandler, scope);
        this.deserializeStrategies = deserializeStrategies;
    }

    @Override
    protected IObject getEnvironment(final ChannelHandlerContext ctx, final FullHttpRequest request) throws Exception {
        IObject environment = deserializeStrategies.get(request.headers().get(HttpHeaders.Names.CONTENT_TYPE))
                .deserialize(request);
        IFieldName contextFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "context");
        IFieldName requestFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "request");
        IFieldName channelFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "channel");
        IFieldName headersFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "headers");
        IFieldName cookiesFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "cookies");

        IChannelHandler channelHandler = IOC.resolve(Keys.getOrAdd(ChannelHandlerNetty.class.getCanonicalName()), ctx);
        //create context of the MP
        IObject context = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()));
        context.setValue(channelFieldName, channelHandler);
        context.setValue(cookiesFieldName, new ArrayList<IObject>());
        context.setValue(headersFieldName, new ArrayList<IObject>());
        //create environment
        environment.setValue(contextFieldName, context);
        environment.setValue(requestFieldName, request);
        return environment;
    }
}
