package info.smart_tools.smartactors.endpoint_components_netty.client_context_binding_handlers;

import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IDefaultMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandlerCallback;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.exception.MessageHandlerException;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.helpers.IBypassMessageHandler;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import io.netty.channel.Channel;

/**
 * Binds a request object (passed as source message) to a channel the request bound to channel may be accessed later
 * using {@link StoreBoundRequestHandler}.
 *
 * @param <TDst>
 * @param <TChan>
 */
public class BindRequestToChannelHandler<TDst, TChan extends Channel>
        implements IBypassMessageHandler<IDefaultMessageContext<IObject, TDst, TChan>> {
    @Override
    public void handle(
            final IMessageHandlerCallback<IDefaultMessageContext<IObject, TDst, TChan>> next,
            final IDefaultMessageContext<IObject, TDst, TChan> context)
                throws MessageHandlerException {
        TChan channel = context.getConnectionContext();
        IObject request = context.getSrcMessage();

        channel.attr(AttributeKeys.REQUEST_ATTRIBUTE_KEY).set(request);

        try {
            next.handle(context);
        } catch (MessageHandlerException | RuntimeException e) {
            channel.attr(AttributeKeys.REQUEST_ATTRIBUTE_KEY).compareAndSet(request, null);
            throw e;
        }
    }
}
