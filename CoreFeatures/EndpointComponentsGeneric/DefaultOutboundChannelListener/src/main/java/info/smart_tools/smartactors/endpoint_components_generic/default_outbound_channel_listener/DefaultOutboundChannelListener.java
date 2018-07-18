package info.smart_tools.smartactors.endpoint_components_generic.default_outbound_channel_listener;

import info.smart_tools.smartactors.endpoint_interfaces.ioutbound_connection_channel.IOutboundConnectionChannel;
import info.smart_tools.smartactors.endpoint_interfaces.ioutbound_connection_channel.IOutboundConnectionChannelListener;
import info.smart_tools.smartactors.endpoint_interfaces.ioutbound_connection_channel.exceptions.OutboundChannelListenerException;

import java.util.Map;

/**
 * Implementation of {@link IOutboundConnectionChannelListener} that stores all connected channels in a {@link Map}.
 */
public class DefaultOutboundChannelListener implements IOutboundConnectionChannelListener {
    private final Map<Object, IOutboundConnectionChannel> channelsStorage;

    /**
     * The constructor.
     *
     * @param channelsStorage map from channel identifier to channel
     */
    public DefaultOutboundChannelListener(final Map<Object, IOutboundConnectionChannel> channelsStorage) {
        this.channelsStorage = channelsStorage;
    }

    @Override
    public void onConnect(final Object id, final IOutboundConnectionChannel channel)
            throws OutboundChannelListenerException {
        channelsStorage.put(id, channel);
    }

    @Override
    public void onDisconnect(final Object id)
            throws OutboundChannelListenerException {
        channelsStorage.remove(id);
    }
}
