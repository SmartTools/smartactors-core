package info.smart_tools.smartactors.endpoint_components_netty.ssl_channel_initialization_handler;

import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IDefaultMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandlerCallback;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.exception.MessageHandlerException;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.helpers.IBypassMessageHandler;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.handler.ssl.SslContext;

/**
 * Handler that adds SSL handler to Netty pipeline.
 *
 * @param <T>
 */
public class SslChannelInitializationHandler<T extends IDefaultMessageContext<Channel, ?, ?>>
        implements IBypassMessageHandler<T> {
    private final SslContext sslContext;

    /**
     * The constructor.
     *
     * @param context the SSL context tha will serve as factory of SSL handlers
     */
    public SslChannelInitializationHandler(final SslContext context) {
        this.sslContext = context;
    }

    @Override
    public void handle(
            final IMessageHandlerCallback<T> next,
            final T context) throws MessageHandlerException {
        context.getSrcMessage().pipeline()
                .addFirst("ssl", sslContext.newHandler(ByteBufAllocator.DEFAULT));
        next.handle(context);
    }
}
