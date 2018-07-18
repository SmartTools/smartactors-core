package info.smart_tools.smartactors.endpoint_components_netty.default_channel_initialization_handlers;

import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IDefaultMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandlerCallback;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

/**
 * Test for {@link HTTPServerChannelSetupHandler}.
 */
public class HTTPServerChannelSetupHandlerTest {
    @Test public void Should_addHandlersToPipeline() throws Exception {
        Channel channelMock = mock(Channel.class);
        ChannelPipeline channelPipelineMock = mock(ChannelPipeline.class);
        IDefaultMessageContext<Channel, ?, ?> contextMock
                = mock(IDefaultMessageContext.class);
        IMessageHandlerCallback<IDefaultMessageContext<Channel, ?, ?>> callback
                = mock(IMessageHandlerCallback.class);

        when(contextMock.getSrcMessage()).thenReturn(channelMock);
        when(channelMock.pipeline()).thenReturn(channelPipelineMock);

        doAnswer(invocationOnMock -> {
            ArgumentCaptor<ChannelHandler> argumentCaptor = ArgumentCaptor.forClass(ChannelHandler.class);
            verify(channelPipelineMock, atLeast(1))
                    .addLast(argumentCaptor.capture());
            assertTrue(argumentCaptor.getAllValues().stream().anyMatch(h -> h instanceof HttpServerCodec));
            assertTrue(argumentCaptor.getAllValues().stream().anyMatch(h -> h instanceof HttpObjectAggregator));

            return null;
        }).when(callback).handle(same(contextMock));

        new HTTPServerChannelSetupHandler<>(4096).handle(callback, contextMock);

        verify(callback).handle(same(contextMock));
    }
}
