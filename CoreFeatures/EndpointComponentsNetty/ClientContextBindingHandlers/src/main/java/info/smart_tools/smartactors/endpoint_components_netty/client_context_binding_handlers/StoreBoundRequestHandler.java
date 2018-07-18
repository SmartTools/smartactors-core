package info.smart_tools.smartactors.endpoint_components_netty.client_context_binding_handlers;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IDefaultMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandlerCallback;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.exception.MessageHandlerException;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.helpers.IBypassMessageHandler;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import io.netty.channel.Channel;

/**
 * Stores request object bound to the channel in inbound internal message environment.
 *
 * <pre>
 *  {
 *    ...
 *
 *    "request": {
 *      // .. request bound to a channel ..
 *    }
 *  }
 * </pre>
 *
 * @param <TMsg>
 * @param <TChan>
 */
public class StoreBoundRequestHandler<TMsg, TChan extends Channel>
        implements IBypassMessageHandler<IDefaultMessageContext<TMsg, IObject, TChan>> {
    private final IFieldName requestFN;

    /**
     * The constructor.
     *
     * @throws ResolutionException if error occurs resolving any dependency
     */
    public StoreBoundRequestHandler()
            throws ResolutionException {
        requestFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "request");
    }

    @Override
    public void handle(
            final IMessageHandlerCallback<IDefaultMessageContext<TMsg, IObject, TChan>> next,
            final IDefaultMessageContext<TMsg, IObject, TChan> ctx)
                throws MessageHandlerException {
        try {
            IObject request = ctx.getConnectionContext().attr(AttributeKeys.REQUEST_ATTRIBUTE_KEY).get();

            ctx.getDstMessage().setValue(requestFN, request);
        } catch (ChangeValueException | InvalidArgumentException e) {
            throw new MessageHandlerException(e);
        }

        next.handle(ctx);
    }
}
