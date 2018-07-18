package info.smart_tools.smartactors.endpoint_components_netty.default_channel_initialization_handlers;

import info.smart_tools.smartactors.endpoint_components_netty.inbound_netty_channel_handler.InboundNettyChannelHandler;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IDefaultMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandlerCallback;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.exception.MessageHandlerException;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.helpers.IBypassMessageHandler;
import io.netty.channel.Channel;

/**
 * Handler that appends a {@link InboundNettyChannelHandler} to netty channel pipeline.
 *
 * @param <T>
 */
public class InboundNettyChannelHandlerSetupHandler<T extends IDefaultMessageContext<Channel, ?, ?>>
        implements IBypassMessageHandler<T> {
    private final InboundNettyChannelHandler channelHandler;

    /**
     * The constructor.
     *
     * @param channelHandler the channel handler
     */
    public InboundNettyChannelHandlerSetupHandler(final InboundNettyChannelHandler channelHandler) {
        this.channelHandler = channelHandler;
    }

    @Override
    public void handle(final IMessageHandlerCallback<T> next, final T context)
            throws MessageHandlerException {
        context.getSrcMessage().pipeline().addLast(channelHandler);
        next.handle(context);
    }
}
