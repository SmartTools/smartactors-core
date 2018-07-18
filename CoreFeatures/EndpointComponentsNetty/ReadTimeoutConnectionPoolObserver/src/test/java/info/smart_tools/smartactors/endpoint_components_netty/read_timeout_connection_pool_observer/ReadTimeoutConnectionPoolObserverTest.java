package info.smart_tools.smartactors.endpoint_components_netty.read_timeout_connection_pool_observer;

import info.smart_tools.smartactors.endpoint_components_netty.isocket_connection_pool.ISocketConnectionPoolObserver;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ReadTimeoutConnectionPoolObserverTest {
    private Channel channelMock;
    private ChannelPipeline pipelineMock;

    @Before public void setUp() throws Exception {
        channelMock = mock(Channel.class);
        pipelineMock = mock(ChannelPipeline.class);
        when(channelMock.pipeline()).thenReturn(pipelineMock);
    }

    @Test public void Should_addHandlerWhenChannelAcquiredAndRemoveWhenReleased() throws Exception {
        ISocketConnectionPoolObserver<Channel> observer = new ReadTimeoutConnectionPoolObserver<>(100500);

        observer.onChannelCreated(channelMock);

        verifyZeroInteractions(channelMock);

        observer.onChannelAcquired(channelMock);

        ArgumentCaptor<ChannelHandler> handlerCaptor = ArgumentCaptor.forClass(ChannelHandler.class);
        ArgumentCaptor<String> nameCaptor = ArgumentCaptor.forClass(String.class);

        verify(pipelineMock).addFirst(nameCaptor.capture(), handlerCaptor.capture());

        assertTrue(handlerCaptor.getValue() instanceof ReadTimeoutHandler);

        observer.onChannelReleased(channelMock);

        verify(pipelineMock).remove(eq(nameCaptor.getValue()));
    }
}
