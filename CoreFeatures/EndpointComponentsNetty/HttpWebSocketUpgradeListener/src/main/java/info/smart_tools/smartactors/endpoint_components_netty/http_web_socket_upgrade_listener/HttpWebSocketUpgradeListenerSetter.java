package info.smart_tools.smartactors.endpoint_components_netty.http_web_socket_upgrade_listener;

import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_pipeline.IEndpointPipeline;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IDefaultMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandlerCallback;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.exception.MessageHandlerException;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.helpers.IBypassMessageHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

/**
 * Handler that adds to Netty pipeline handlers required for Web-socket protocol support.
 *
 * <p>
 *  This handler requires a HTTP protocol handlers to be configured by one of previous handlers.
 * </p>
 *
 * @param <TChan>
 * @param <TDst>
 */
public class HttpWebSocketUpgradeListenerSetter<TChan extends Channel, TDst>
        implements IBypassMessageHandler<IDefaultMessageContext<TChan, TDst, TChan>> {
    private final IEndpointPipeline<IDefaultMessageContext<Channel, Void, Channel>> upgradeProcessPipeline;
    private final String webSocketPath;

    /**
     * Handler that embeds in Netty pipeline and intercepts Web-socket upgrade events.
     */
    @ChannelHandler.Sharable
    private final class UpgradeEventListener extends ChannelInboundHandlerAdapter {

        @Override
        public void userEventTriggered(final ChannelHandlerContext ctx, final Object evt)
                throws Exception {
            if (evt == WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE) {
                IDefaultMessageContext<Channel, Void, Channel> messageContext
                        = upgradeProcessPipeline.getContextFactory().execute();
                Channel channel = ctx.channel();
                messageContext.setSrcMessage(channel);
                messageContext.setConnectionContext(channel);
                upgradeProcessPipeline.getInputCallback().handle(messageContext);
            }

            super.userEventTriggered(ctx, evt);
        }
    }

    private final UpgradeEventListener upgradeEventListener = new UpgradeEventListener();

    /**
     * The constructor.
     *
     * @param upgradeProcessPipeline the pipeline to notify when upgrade to Web-socket occurs at channel
     * @param webSocketPath          path of web socket
     */
    public HttpWebSocketUpgradeListenerSetter(
            final IEndpointPipeline<IDefaultMessageContext<Channel, Void, Channel>> upgradeProcessPipeline,
            final String webSocketPath) {
        this.upgradeProcessPipeline = upgradeProcessPipeline;
        this.webSocketPath = webSocketPath;
    }

    @Override
    public void handle(
        final IMessageHandlerCallback<IDefaultMessageContext<TChan, TDst, TChan>> next,
        final IDefaultMessageContext<TChan, TDst, TChan> context)
            throws MessageHandlerException {
        context.getConnectionContext().pipeline().addLast(
                new WebSocketServerProtocolHandler(webSocketPath),
                upgradeEventListener
        );

        next.handle(context);
    }
}
