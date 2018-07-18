package info.smart_tools.smartactors.endpoint_components_netty.wrap_inbound_netty_message_to_message_byte_array_message_handler;

import info.smart_tools.smartactors.endpoint_interfaces.imessage_byte_array.IInboundMessageByteArray;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IDefaultMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandler;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandlerCallback;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.exception.MessageHandlerException;
import io.netty.buffer.ByteBufHolder;

/**
 * Message handler that wraps a inbound Netty message into a {@link IInboundMessageByteArray}.
 *
 * @param <TNettyMsg>
 * @param <TDstMessage>
 * @param <TCtx>
 */
public class WrapInboundNettyMessageToMessageByteArrayMessageHandler<TNettyMsg extends ByteBufHolder, TDstMessage, TCtx>
        implements IMessageHandler<
            IDefaultMessageContext<TNettyMsg, TDstMessage, TCtx>,
            IDefaultMessageContext<IInboundMessageByteArray<TNettyMsg>, TDstMessage, TCtx>> {

    @Override
    public void handle(final IMessageHandlerCallback<IDefaultMessageContext<IInboundMessageByteArray<TNettyMsg>, TDstMessage, TCtx>> next,
                       final IDefaultMessageContext<TNettyMsg, TDstMessage, TCtx> context)
            throws MessageHandlerException {
        TNettyMsg nettyMsg = context.getSrcMessage();

        IDefaultMessageContext<IInboundMessageByteArray<TNettyMsg>, TDstMessage, TCtx> nextContext
                = context.cast(IDefaultMessageContext.class);

        nextContext.setSrcMessage(new NettyInboundMessageByteArray<>(nettyMsg));

        next.handle(nextContext);
    }
}
