package info.smart_tools.smartactors.endpoint_components_netty.default_channel_initialization_handlers;

import info.smart_tools.smartactors.base.interfaces.iaction.IFunction;
import info.smart_tools.smartactors.base.interfaces.iaction.IFunction0;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IDefaultMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandlerCallback;
import info.smart_tools.smartactors.endpoint_interfaces.ioutbound_connection_channel.IOutboundConnectionChannel;
import info.smart_tools.smartactors.endpoint_interfaces.ioutbound_connection_channel.IOutboundConnectionChannelListener;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.Attribute;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.*;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

/**
 * Test for {@link OutboundChannelCreator}.
 */
public class OutboundChannelCreatorTest {
    private IFunction0<String> idProviderMock;
    private IOutboundConnectionChannelListener connectionChannelListener;
    private IFunction<Channel, IOutboundConnectionChannel> channelProvider;
    private Channel[] nettyChannels;
    private IOutboundConnectionChannel[] channels;

    @Before public void setUp() throws Exception {
        idProviderMock = mock(IFunction0.class);
        when(idProviderMock.execute())
                .thenReturn("id-1").thenReturn("id-2")
                .thenThrow(AssertionError.class);

        connectionChannelListener = mock(IOutboundConnectionChannelListener.class);

        nettyChannels = new Channel[]{
                mock(Channel.class),
                mock(Channel.class),
        };
        channels = new IOutboundConnectionChannel[]{
                mock(IOutboundConnectionChannel.class),
                mock(IOutboundConnectionChannel.class),
        };
        channelProvider = mock(IFunction.class);
        when(channelProvider.execute(same(nettyChannels[0]))).thenReturn(channels[0]);
        when(channelProvider.execute(same(nettyChannels[1]))).thenReturn(channels[1]);

        when(nettyChannels[0].attr(same(ChannelAttributes.OUTBOUND_CHANNEL_ID_KEY)))
                .thenReturn(mock(Attribute.class));
        when(nettyChannels[0].closeFuture()).thenReturn(mock(ChannelFuture.class));
    }

    @Test public void Should_createChannelAssociateItWithNettyChannelAndNotifyListener() throws Exception {
        OutboundChannelCreator channelCreator = new OutboundChannelCreator(
                idProviderMock, channelProvider, connectionChannelListener);

        IDefaultMessageContext context = mock(IDefaultMessageContext.class);
        when(context.getSrcMessage()).thenReturn(nettyChannels[0]);
        IMessageHandlerCallback callback = mock(IMessageHandlerCallback.class);

        when(context.getConnectionContext()).thenReturn(nettyChannels[0]);

        doAnswer(invocationOnMock -> {
            verify(connectionChannelListener)
                    .onConnect(eq("id-1"), same(channels[0]));
            verify(nettyChannels[0].attr(ChannelAttributes.OUTBOUND_CHANNEL_ID_KEY))
                    .set(eq("id-1"));
            verify(nettyChannels[0].closeFuture())
                    .addListener(any());

            verifyNoMoreInteractions(connectionChannelListener);

            return null;
        }).when(callback).handle(same(context));

        channelCreator.handle(callback, context);

        verify(callback).handle(same(context));

        ArgumentCaptor<ChannelFutureListener> argumentCaptor = ArgumentCaptor.forClass(ChannelFutureListener.class);
        verify(nettyChannels[0].closeFuture()).addListener(argumentCaptor.capture());

        when(nettyChannels[0].closeFuture().channel()).thenReturn(nettyChannels[0]);
        when(nettyChannels[0].attr(ChannelAttributes.OUTBOUND_CHANNEL_ID_KEY).getAndRemove())
                .thenReturn("id-1");

        argumentCaptor.getValue().operationComplete(nettyChannels[0].closeFuture());

        verify(connectionChannelListener).onDisconnect(eq("id-1"));
    }
}
