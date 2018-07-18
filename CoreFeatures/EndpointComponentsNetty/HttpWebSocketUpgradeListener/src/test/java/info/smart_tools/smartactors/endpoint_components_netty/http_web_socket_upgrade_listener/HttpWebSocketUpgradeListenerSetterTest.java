package info.smart_tools.smartactors.endpoint_components_netty.http_web_socket_upgrade_listener;

import info.smart_tools.smartactors.base.interfaces.iaction.IFunction0;
import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_pipeline.IEndpointPipeline;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IDefaultMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandlerCallback;
import io.netty.channel.*;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import org.junit.Test;
import org.mockito.ArgumentCaptor;


import static org.junit.Assert.*;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

/**
 * Test for {@link HttpWebSocketUpgradeListenerSetter}.
 */
public class HttpWebSocketUpgradeListenerSetterTest {
    @Test public void Should_addWebSocketHandlersToNettyChannelPipeline() throws Exception {
        IDefaultMessageContext<Channel, Object, Channel> context
                = mock(IDefaultMessageContext.class);
        IMessageHandlerCallback<IDefaultMessageContext<Channel, Object, Channel>> callback
                = mock(IMessageHandlerCallback.class);
        Channel channel = mock(Channel.class);
        ChannelPipeline pipeline = mock(ChannelPipeline.class);
        IEndpointPipeline<IDefaultMessageContext<Channel, Void, Channel>> upgradePipeline
                = mock(IEndpointPipeline.class);
        IFunction0<IDefaultMessageContext<Channel, Void, Channel>> upCf
                = mock(IFunction0.class);
        IMessageHandlerCallback<IDefaultMessageContext<Channel, Void, Channel>> upCb
                = mock(IMessageHandlerCallback.class);
        IDefaultMessageContext<Channel, Void, Channel> upCtx
                = mock(IDefaultMessageContext.class);

        when(context.getConnectionContext()).thenReturn(channel);
        when(context.getSrcMessage()).thenReturn(channel);
        when(channel.pipeline()).thenReturn(pipeline);
        when(upgradePipeline.getContextFactory()).thenReturn(upCf);
        when(upgradePipeline.getInputCallback()).thenReturn(upCb);

        when(upCf.execute())
                .thenReturn(upCtx)
                .thenThrow(AssertionError.class);

        doAnswer(invocationOnMock -> {
            verify(pipeline).addLast(anyVararg());
            return null;
        }).when(callback).handle(same(context));

        new HttpWebSocketUpgradeListenerSetter<>(upgradePipeline, "/wspath")
                .handle(callback, context);

        verify(callback).handle(same(context));

        ArgumentCaptor<ChannelHandler> argumentCaptor = ArgumentCaptor.forClass(ChannelHandler.class);
        verify(pipeline).addLast(argumentCaptor.capture());

        assertTrue(argumentCaptor.getAllValues().get(0) instanceof WebSocketServerProtocolHandler);

        ChannelHandler eventHandler = argumentCaptor.getAllValues().get(1);

        assertTrue(eventHandler.getClass().isAnnotationPresent(ChannelHandler.Sharable.class));
        assertTrue(eventHandler instanceof ChannelInboundHandler);

        ChannelInboundHandler eventHandler0 = ((ChannelInboundHandler) eventHandler);

        ChannelHandlerContext ctx = mock(ChannelHandlerContext.class);
        when(ctx.channel()).thenReturn(channel);

        eventHandler0.userEventTriggered(ctx, new Object());
        verify(upgradePipeline.getContextFactory(), times(0)).execute();

        eventHandler0.userEventTriggered(ctx, WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE);

        verify(upCb).handle(same(upCtx));
        verify(upCtx).setSrcMessage(same(channel));
        verify(upCtx).setConnectionContext(same(channel));
    }
}
