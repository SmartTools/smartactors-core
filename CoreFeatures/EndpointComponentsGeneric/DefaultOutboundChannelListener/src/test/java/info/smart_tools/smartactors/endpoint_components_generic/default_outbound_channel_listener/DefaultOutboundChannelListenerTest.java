package info.smart_tools.smartactors.endpoint_components_generic.default_outbound_channel_listener;

import info.smart_tools.smartactors.endpoint_interfaces.ioutbound_connection_channel.IOutboundConnectionChannel;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class DefaultOutboundChannelListenerTest {
    @Test public void Should_justStoreChannelsImTheMap() throws Exception {
        Map<Object, IOutboundConnectionChannel> map = new HashMap<>();

        DefaultOutboundChannelListener listener = new DefaultOutboundChannelListener(map);

        IOutboundConnectionChannel channel;
        listener.onConnect("the-id", channel = mock(IOutboundConnectionChannel.class));

        assertSame(channel, map.get("the-id"));

        listener.onDisconnect("the-id");

        assertTrue(map.isEmpty());
    }
}
