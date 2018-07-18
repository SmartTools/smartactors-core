package info.smart_tools.smartactors.endpoint_components_generic.store_raw_inbound_message_handler;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_byte_array.IMessageByteArray;
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

/**
 * Stores raw external inbound message in inbound internal message.
 *
 * <pre>
 * {
 *     "rawMessage": TMsg::TMessage {},
 *     ...
 * }
 * </pre>
 *
 * @param <TMsg>
 * @param <TCtx>
 */
public class StoreRawInboundMessageHandler<TMsg extends IMessageByteArray<?>, TCtx>
        implements IBypassMessageHandler<IDefaultMessageContext<TMsg, IObject, TCtx>> {
    private IFieldName rawMessageFN;

    public StoreRawInboundMessageHandler()
            throws ResolutionException {
        rawMessageFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "rawMessage");
    }

    @Override
    public void handle(
            final IMessageHandlerCallback<IDefaultMessageContext<TMsg, IObject, TCtx>> next,
            final IDefaultMessageContext<TMsg, IObject, TCtx> context)
                throws MessageHandlerException {
        try {
            context.getDstMessage().setValue(rawMessageFN, context.getSrcMessage().getMessage());
        } catch (ChangeValueException | InvalidArgumentException e) {
            throw new MessageHandlerException(e);
        }

        next.handle(context);
    }
}
