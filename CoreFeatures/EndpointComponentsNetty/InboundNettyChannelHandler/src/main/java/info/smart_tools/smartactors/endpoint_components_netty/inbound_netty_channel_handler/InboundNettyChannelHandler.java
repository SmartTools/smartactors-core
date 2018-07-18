package info.smart_tools.smartactors.endpoint_components_netty.inbound_netty_channel_handler;

import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_pipeline.IEndpointPipeline;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IDefaultMessageContext;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Netty channel handler that sends received messages to message handler pipeline.
 *
 * @param <TNettyMsg>
 */
@ChannelHandler.Sharable
public class InboundNettyChannelHandler<TNettyMsg> extends SimpleChannelInboundHandler<TNettyMsg> {
    private final IEndpointPipeline<IDefaultMessageContext<TNettyMsg, Void, Channel>> mainPipeline;
    private final IEndpointPipeline<IDefaultMessageContext<Throwable, Void, Channel>> errorPipeline;

    /**
     * The constructor.
     * @param mainPipeline     pipeline that should process inbound messages
     * @param errorPipeline    pipeline that should process errors
     * @param imClass          type of inbound Netty message
     */
    public InboundNettyChannelHandler(
            final IEndpointPipeline<IDefaultMessageContext<TNettyMsg, Void, Channel>> mainPipeline,
            final IEndpointPipeline<IDefaultMessageContext<Throwable, Void, Channel>> errorPipeline,
            final Class<TNettyMsg> imClass) {
        super(imClass);
        this.mainPipeline = mainPipeline;
        this.errorPipeline = errorPipeline;
    }

    protected void channelRead0(
            final ChannelHandlerContext ctx,
            final TNettyMsg msg) throws Exception {
        IDefaultMessageContext<TNettyMsg, Void, Channel> context
                = mainPipeline.getContextFactory().execute();

        context.setSrcMessage(msg);
        context.setConnectionContext(ctx.channel());
        context.setDstMessage(null);

        mainPipeline.getInputCallback().handle(context);
    }

    // Forward-compatibility method
    protected void messageReceived(
            final ChannelHandlerContext ctx,
            final TNettyMsg msg) throws Exception {
        channelRead0(ctx, msg);
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause)
            throws Exception {
        IDefaultMessageContext<Throwable, Void, Channel> context
                = errorPipeline.getContextFactory().execute();

        context.setSrcMessage(cause);
        context.setConnectionContext(ctx.channel());
        context.setDstMessage(null);

        errorPipeline.getInputCallback().handle(context);

        super.exceptionCaught(ctx, cause);
    }
}
