package info.smart_tools.smartactors.http_endpoint.channel_handler_netty;

import info.smart_tools.smartactors.endpoint.interfaces.ichannel_handler.IChannelHandler;
import io.netty.channel.ChannelHandlerContext;

/**
 * Adapter for netty {@link ChannelHandlerContext}
 */
public class ChannelHandlerNetty implements IChannelHandler<ChannelHandlerContext> {

    private ChannelHandlerContext channelHandler;

    @Override
    public void init(final ChannelHandlerContext handler) {
        this.channelHandler = handler;
    }

    @Override
    public void send(final Object response) {
        channelHandler.writeAndFlush(response);
    }

    @Override
    public ChannelHandlerContext getHandler() {
        return this.channelHandler;
    }
}
