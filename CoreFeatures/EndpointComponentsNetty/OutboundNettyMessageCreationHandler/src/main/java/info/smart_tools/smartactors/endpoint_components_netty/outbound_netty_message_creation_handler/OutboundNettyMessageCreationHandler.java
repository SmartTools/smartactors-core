package info.smart_tools.smartactors.endpoint_components_netty.outbound_netty_message_creation_handler;

import info.smart_tools.smartactors.base.interfaces.iaction.IFunction0;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.FunctionExecutionException;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_byte_array.IMessageByteArray;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_byte_array.IOutboundMessageByteArray;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IDefaultMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandler;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandlerCallback;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.exception.MessageHandlerException;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.Channel;

/**
 * Message handler that creates a outbound Netty message and wraps it into a
 * {@link info.smart_tools.smartactors.endpoint_interfaces.imessage_byte_array.IOutboundMessageByteArray}.
 *
 * @param <TDstMessage> type of outbound Netty message
 * @param <TSrcMessage> type of source message
 * @param <TSrcContext>
 * @param <TDstContext>
 */
public class OutboundNettyMessageCreationHandler<
        TDstMessage extends ByteBufHolder,
        TSrcMessage,
        TSrcContext extends IDefaultMessageContext<TSrcMessage, Void, Channel>,
        TDstContext extends IDefaultMessageContext<TSrcMessage, IOutboundMessageByteArray<TDstMessage>, Channel>>
            implements IMessageHandler<TSrcContext, TDstContext> {
    private final IFunction0<TDstMessage> messageFactory;

    /**
     * The constructor.
     *
     * @param messageFactory function providing Netty messages
     */
    public OutboundNettyMessageCreationHandler(final IFunction0<TDstMessage> messageFactory) {
        this.messageFactory = messageFactory;
    }

    @Override
    public void handle(
            final IMessageHandlerCallback<TDstContext> next,
            final TSrcContext context)
                throws MessageHandlerException {
        try {
            TDstContext dstContext = context.cast(IDefaultMessageContext.class);

            dstContext.setDstMessage(new OutboundNettyMessageByteArray<>(messageFactory.execute()));

            next.handle(dstContext);
        } catch (FunctionExecutionException e) {
            throw new MessageHandlerException(e);
        }
    }
}
