package info.smart_tools.smartactors.endpoint_components_netty.channel_pool_handlers;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.endpoint_components_netty.isocket_connection_pool.ISocketConnectionPool;
import info.smart_tools.smartactors.endpoint_components_netty.isocket_connection_pool.exception.SocketConnectionPoolException;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IDefaultMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandler;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandlerCallback;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.exception.MessageHandlerException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import io.netty.channel.Channel;

import java.net.SocketAddress;

public class AcquireChannelFromPoolHandler<TDst, TChan extends Channel, TAddr extends SocketAddress>
        implements IMessageHandler<IDefaultMessageContext<IObject, TDst, Void>, IDefaultMessageContext<IObject, TDst, TChan>> {
    private final ISocketConnectionPool<TChan, TAddr> pool;
    private final IFieldName addressFN;

    public AcquireChannelFromPoolHandler(final ISocketConnectionPool<TChan, TAddr> pool)
            throws ResolutionException {
        this.pool = pool;
        addressFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "address");
    }

    @Override
    public void handle(
            final IMessageHandlerCallback<IDefaultMessageContext<IObject, TDst, TChan>> next,
            final IDefaultMessageContext<IObject, TDst, Void> context)
                throws MessageHandlerException {
        IDefaultMessageContext<IObject, TDst, TChan> context1 = context.cast(IDefaultMessageContext.class);

        try {
            @SuppressWarnings({"unchecked"})
            TAddr address = (TAddr) context1.getSrcMessage().getValue(addressFN);
            TChan channel = pool.getChannel(address);
            channel.attr(AttributeKeys.POOL_ATTRIBUTE_KEY).set(pool);
            context1.setConnectionContext(channel);
        } catch (SocketConnectionPoolException | ReadValueException | InvalidArgumentException e) {
            throw new MessageHandlerException(e);
        }

        next.handle(context1);
    }
}
