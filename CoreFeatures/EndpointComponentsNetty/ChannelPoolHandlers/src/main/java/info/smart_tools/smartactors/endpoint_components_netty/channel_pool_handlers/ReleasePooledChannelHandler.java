package info.smart_tools.smartactors.endpoint_components_netty.channel_pool_handlers;

import info.smart_tools.smartactors.endpoint_components_netty.isocket_connection_pool.ISocketConnectionPool;
import info.smart_tools.smartactors.endpoint_components_netty.isocket_connection_pool.exception.SocketConnectionPoolException;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IDefaultMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandler;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandlerCallback;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.exception.MessageHandlerException;
import io.netty.channel.Channel;

public class ReleasePooledChannelHandler<TSrc, TDst, TChan extends Channel>
        implements IMessageHandler<IDefaultMessageContext<TSrc, TDst, TChan>, IDefaultMessageContext<TSrc, TDst, Void>> {
    @Override
    public void handle(
            final IMessageHandlerCallback<IDefaultMessageContext<TSrc, TDst, Void>> next,
            final IDefaultMessageContext<TSrc, TDst, TChan> context)
                throws MessageHandlerException {
        TChan channel = context.getConnectionContext();
        @SuppressWarnings({"unchecked"})
        ISocketConnectionPool<TChan, ?> pool
                = (ISocketConnectionPool<TChan, ?>) channel.attr(AttributeKeys.POOL_ATTRIBUTE_KEY).getAndRemove();

        context.setConnectionContext(null);

        if (null != pool) {
            try {
                pool.recycleChannel(channel);
            } catch (SocketConnectionPoolException e) {
                throw new MessageHandlerException(e);
            }
        } else {
            channel.close();
            throw new MessageHandlerException("Invalid use of ReleasePooledChannelHandler: no pool reference stored in channel.");
        }

        next.handle(context.cast(IDefaultMessageContext.class));
    }
}
