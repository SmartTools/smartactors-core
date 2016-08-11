package info.smart_tools.smartactors.core.http_request_handler;


import info.smart_tools.smartactors.core.ideserialize_strategy.IDeserializeStrategy;
import info.smart_tools.smartactors.core.channel_handler_netty.ChannelHandlerNetty;
import info.smart_tools.smartactors.core.endpoint_handler.EndpointHandler;
import info.smart_tools.smartactors.core.endpoint_handler.exceptions.EndpointException;
import info.smart_tools.smartactors.core.ichannel_handler.IChannelHandler;
import info.smart_tools.smartactors.core.ienvironment_handler.IEnvironmentHandler;
import info.smart_tools.smartactors.core.ifield_name.IFieldName;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.iscope.IScope;
import info.smart_tools.smartactors.core.message_processing.IReceiverChain;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

import java.util.ArrayList;

/**
 * Endpoint handler for HTTP requests.
 */
public class HttpRequestHandler extends EndpointHandler<ChannelHandlerContext, FullHttpRequest> {
    private IFieldName messageFieldName;
    private IFieldName contextFieldName;
    private IFieldName requestFieldName;
    private IFieldName channelFieldName;
    private IFieldName headersFieldName;
    private IFieldName cookiesFieldName;

    /**
     * Constructor for HttpRequestHandler
     *
     * @param scope              scope for HttpRequestHandler
     * @param environmentHandler handler for environment
     * @param receiver           chain, that should receive message
     * @throws EndpointException if {@link IOC} can`t resolve {@link IFieldName}
     */
    public HttpRequestHandler(
            final IScope scope, final IEnvironmentHandler environmentHandler, final IReceiverChain receiver) throws EndpointException {
        super(receiver, environmentHandler, scope);
        try {
            messageFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "message");
            contextFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "context");
            requestFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "request");
            channelFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "channel");
            headersFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "headers");
            cookiesFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "cookies");
        } catch (ResolutionException e) {
            throw new EndpointException("Failed to resolve \"field name\"", e);
        }
    }

    @Override
    public IObject getEnvironment(final ChannelHandlerContext ctx, final FullHttpRequest request) throws Exception {
        IDeserializeStrategy deserializeStrategy = IOC.resolve(Keys.getOrAdd(IDeserializeStrategy.class.getCanonicalName()), request);
        IObject message = deserializeStrategy.deserialize(request);
        IObject environment = IOC.resolve(Keys.getOrAdd("EmptyIObject"));
        IChannelHandler channelHandler = IOC.resolve(Keys.getOrAdd(ChannelHandlerNetty.class.getCanonicalName()), ctx);
        //create context of the MP
        IObject context = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()));
        context.setValue(channelFieldName, channelHandler);
        context.setValue(cookiesFieldName, new ArrayList<IObject>());
        context.setValue(headersFieldName, new ArrayList<IObject>());
        context.setValue(requestFieldName, request);
        //create environment
        environment.setValue(messageFieldName, message);
        environment.setValue(contextFieldName, context);
        return environment;
    }
}
