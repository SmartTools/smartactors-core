package info.smart_tools.smartactors.endpoint_components_netty.send_netty_message_message_handler;

import info.smart_tools.smartactors.endpoint_interfaces.imessage_byte_array.IOutboundMessageByteArray;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IDefaultMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandlerCallback;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.exception.MessageHandlerException;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.helpers.ITerminalMessageHandler;
import io.netty.channel.Channel;

/**
 * Message handler that sends a outbound external messages.
 *
 * @param <TMessage>
 * @param <T>
 */
public class SendNettyMessageMessageHandler<TMessage, T extends IDefaultMessageContext<?, IOutboundMessageByteArray<TMessage>, Channel>>
        implements ITerminalMessageHandler<T> {

    @Override
    public void handle(final IMessageHandlerCallback<IMessageContext> next, final T context)
            throws MessageHandlerException {
        context.getConnectionContext().writeAndFlush(context.getDstMessage().getMessage());
    }
}
