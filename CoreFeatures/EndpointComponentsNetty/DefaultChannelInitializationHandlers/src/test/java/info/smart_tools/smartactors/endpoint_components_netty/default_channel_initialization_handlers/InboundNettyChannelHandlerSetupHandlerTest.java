package info.smart_tools.smartactors.endpoint_components_netty.default_channel_initialization_handlers;

import info.smart_tools.smartactors.endpoint_components_netty.inbound_netty_channel_handler.InboundNettyChannelHandler;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IDefaultMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandlerCallback;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

/**
 * Test for {@link InboundNettyChannelHandlerSetupHandler}.
 */
public class InboundNettyChannelHandlerSetupHandlerTest {
    @Test public void Should_addHandlerToNettyPipeline() throws Exception {
        InboundNettyChannelHandler handler = mock(InboundNettyChannelHandler.class);
        IDefaultMessageContext<Channel, ?, ?> contextMock = mock(IDefaultMessageContext.class);
        Channel channelMock = mock(Channel.class);
        ChannelPipeline pipelineMock = mock(ChannelPipeline.class);

        when(contextMock.getSrcMessage()).thenReturn(channelMock);
        when(channelMock.pipeline()).thenReturn(pipelineMock);

        IMessageHandlerCallback<IDefaultMessageContext<Channel, ?, ?>> callback = mock(IMessageHandlerCallback.class);

        doAnswer(invocationOnMock -> {
            verify(pipelineMock).addLast(same(handler));
            return null;
        }).when(callback).handle(same(contextMock));

        new InboundNettyChannelHandlerSetupHandler<>(handler).handle(callback, contextMock);

        verify(callback).handle(same(contextMock));
    }
}
