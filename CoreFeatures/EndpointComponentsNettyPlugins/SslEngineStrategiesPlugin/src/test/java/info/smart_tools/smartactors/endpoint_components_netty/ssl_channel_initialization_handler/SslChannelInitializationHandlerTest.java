package info.smart_tools.smartactors.endpoint_components_netty.ssl_channel_initialization_handler;

import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IDefaultMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandlerCallback;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.*;

public class SslChannelInitializationHandlerTest {
    private SslContext sslContext;
    private SslHandler sslHandler;
    private IDefaultMessageContext messageContext;
    private Channel channel;
    private ChannelPipeline channelPipeline;
    private IMessageHandlerCallback callback;

    @Before public void setUp() {
        sslContext = mock(SslContext.class);
        sslHandler = mock(SslHandler.class);
        messageContext = mock(IDefaultMessageContext.class);
        channel = mock(Channel.class);
        channelPipeline = mock(ChannelPipeline.class);
        callback = mock(IMessageHandlerCallback.class);

        when(sslContext.newHandler(notNull(ByteBufAllocator.class)))
                .thenReturn(sslHandler)
                .thenThrow(new AssertionError("Should be called only once."));
        when(messageContext.getSrcMessage()).thenReturn(channel);
        when(channel.pipeline()).thenReturn(channelPipeline);
    }

    @Ignore("Can not mock SslContext")
    @Test public void Should_setupSslHandler() throws Exception {
        doAnswer(invocationOnMock -> {
            verify(channelPipeline).addFirst(same(sslHandler));
            return null;
        }).when(callback).handle(any());

        new SslChannelInitializationHandler(sslContext).handle(callback, messageContext);

        verify(callback).handle(same(messageContext));
    }
}
