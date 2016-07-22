package info.smart_tools.smartactors.core;


import info.smart_tools.smartactors.core.endpoint_handler.EndpointHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Adapter for {@link EndpointHandler} in order to use it in netty {@link io.netty.channel.ChannelHandler}
 * TODO: handle exceptions properly: send to some special channel or just log them.
 * @param <TRequest> type of request written to channel
 */
@ChannelHandler.Sharable
public class EndpointChannelInboundHandler<TRequest> extends SimpleChannelInboundHandler<TRequest> {
    private EndpointHandler<ChannelHandlerContext, TRequest> handler;

    /**
     * Constructor
     * @param handler Handler, that will receive requests
     * @param requestClass Type of the request
     */
    public EndpointChannelInboundHandler(final EndpointHandler<ChannelHandlerContext, TRequest> handler,
                                         final Class<? extends TRequest> requestClass) {
        super(requestClass);
        this.handler = handler;
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final TRequest request) throws Exception {
        handler.handle(ctx, request);
    }
}
