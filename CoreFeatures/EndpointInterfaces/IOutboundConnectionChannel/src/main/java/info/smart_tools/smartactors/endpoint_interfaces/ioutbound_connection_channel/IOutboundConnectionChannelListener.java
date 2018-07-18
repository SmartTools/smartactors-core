package info.smart_tools.smartactors.endpoint_interfaces.ioutbound_connection_channel;

import info.smart_tools.smartactors.endpoint_interfaces.ioutbound_connection_channel.exceptions.OutboundChannelListenerException;

/**
 * Interface for a object that should be notified on state change of external connections allowing messages to be sent
 * over them.
 */
public interface IOutboundConnectionChannelListener {
    /**
     * Called when new connection is open.
     *
     * @param id      connection identifier
     * @param channel channel to send messages to
     * @throws OutboundChannelListenerException if any error occurs
     */
     void onConnect(Object id, IOutboundConnectionChannel channel)
            throws OutboundChannelListenerException;

    /**
     * Called when a previously open connection gets closed.
     *
     * @param id      connection identifier
     * @throws OutboundChannelListenerException if any error occurs
     */
    void onDisconnect(Object id)
            throws OutboundChannelListenerException;
}
