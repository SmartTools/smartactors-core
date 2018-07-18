package info.smart_tools.smartactors.endpoint_components_netty.default_channel_initialization_handlers;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IDefaultMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandlerCallback;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.exception.MessageHandlerException;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.helpers.IBypassMessageHandler;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import io.netty.channel.Channel;

/**
 * Message handler that stores a identifier of {@link info.smart_tools.smartactors.endpoint_interfaces.ioutbound_connection_channel.IOutboundConnectionChannel
 * outbound channel} associated with the channel received inbound message in context of destination inbound message.
 *
 * @param <TChannel>
 * @param <TSrc>
 */
public class StoreOutboundChannelIdToContext<TChannel extends Channel, TSrc>
        implements IBypassMessageHandler<IDefaultMessageContext<TSrc, IObject, TChannel>> {
    private final IFieldName contextFN;
    private final IFieldName channelIdFN;

    /**
     * The constructor.
     *
     * @throws ResolutionException if error occurs resolving any dependencies
     */
    public StoreOutboundChannelIdToContext() throws ResolutionException {
        contextFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "context");
        channelIdFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "channelId");
    }

    @Override
    public void handle(
        final IMessageHandlerCallback<IDefaultMessageContext<TSrc, IObject, TChannel>> next,
        final IDefaultMessageContext<TSrc, IObject, TChannel> context)
            throws MessageHandlerException {
        try {
            IObject dstContext = (IObject) context.getDstMessage().getValue(contextFN);
            Object channelId = context.getConnectionContext().attr(ChannelAttributes.OUTBOUND_CHANNEL_ID_KEY).get();
            dstContext.setValue(channelIdFN, channelId);
        } catch (ReadValueException | ChangeValueException | InvalidArgumentException e) {
            throw new MessageHandlerException(e);
        }

        next.handle(context);
    }
}
