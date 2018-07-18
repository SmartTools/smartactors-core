package info.smart_tools.smartactors.endpoint_components_netty.inbound_netty_channel_handler;

import info.smart_tools.smartactors.base.interfaces.iaction.IFunction0;
import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_pipeline.IEndpointPipeline;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IDefaultMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandlerCallback;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

/**
 * Test for {@link InboundNettyChannelHandler}.
 */
public class InboundNettyChannelHandlerTest {
    private class Message {}

    private IFunction0<IDefaultMessageContext<Message, Void, Channel>> ctxProvider;
    private IDefaultMessageContext<Message, Void, Channel> ctxMock;
    private IMessageHandlerCallback<IDefaultMessageContext<Message, Void, Channel>> callback;
    private ChannelHandlerContext handlerContext;
    private Channel channel;
    private IEndpointPipeline<IDefaultMessageContext<Message, Void, Channel>> pipeline;

    private IFunction0<IDefaultMessageContext<Throwable, Void, Channel>> errCtxProvider;
    private IDefaultMessageContext<Throwable, Void, Channel> errCtxMock;
    private IMessageHandlerCallback<IDefaultMessageContext<Throwable, Void, Channel>> errCallback;
    private IEndpointPipeline<IDefaultMessageContext<Throwable, Void, Channel>> errPipeline;

    @Before public void setUp() throws Exception {
        ctxProvider = mock(IFunction0.class);
        ctxMock = mock(IDefaultMessageContext.class);
        callback = mock(IMessageHandlerCallback.class);
        handlerContext = mock(ChannelHandlerContext.class);
        channel = mock(Channel.class);

        when(ctxProvider.execute()).thenReturn(ctxMock).thenThrow(AssertionError.class);
        when(handlerContext.channel()).thenReturn(channel);

        pipeline = mock(IEndpointPipeline.class);
        when(pipeline.getContextFactory()).thenReturn(ctxProvider);
        when(pipeline.getInputCallback()).thenReturn(callback);

        errCtxProvider = mock(IFunction0.class);
        errCtxMock = mock(IDefaultMessageContext.class);
        errCallback = mock(IMessageHandlerCallback.class);

        when(errCtxProvider.execute()).thenReturn(errCtxMock).thenThrow(AssertionError.class);

        errPipeline = mock(IEndpointPipeline.class);
        when(errPipeline.getContextFactory()).thenReturn(errCtxProvider);
        when(errPipeline.getInputCallback()).thenReturn(errCallback);
    }

    @Test public void Should_processInboundMessages() throws Exception {
        InboundNettyChannelHandler handler = new InboundNettyChannelHandler<>(
                pipeline, errPipeline, Message.class);

        Message message = new Message();

        doAnswer(invocationOnMock -> {
            verify(ctxMock).setConnectionContext(same(channel));
            verify(ctxMock).setSrcMessage(same(message));

            return null;
        }).when(callback).handle(same(ctxMock));

        handler.channelRead(handlerContext, message);

        verify(callback).handle(same(ctxMock));
    }

    @Test public void Should_processErrors() throws Exception {
        Throwable err = new Throwable();
        InboundNettyChannelHandler handler = new InboundNettyChannelHandler<>(
                pipeline, errPipeline, Message.class);

        doAnswer(invocationOnMock -> {
            verify(errCtxMock).setConnectionContext(same(channel));
            verify(errCtxMock).setSrcMessage(same(err));

            return null;
        }).when(errCallback).handle(same(errCtxMock));

        handler.exceptionCaught(handlerContext, err);

        verify(errCallback).handle(same(errCtxMock));
    }
}
