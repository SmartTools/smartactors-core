package info.smart_tools.smartactors.endpoint_components_netty.inetty_transport_provider;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.endpoint_components_netty.inetty_transport_provider.exceptions.EventLoopGroupCreationException;
import info.smart_tools.smartactors.endpoint_components_netty.inetty_transport_provider.exceptions.InvalidEventLoopGroupException;
import info.smart_tools.smartactors.endpoint_components_netty.inetty_transport_provider.exceptions.UnsupportedChannelTypeException;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFactory;
import io.netty.channel.EventLoopGroup;

/**
 * Interface for object containing methods depending on Netty transport type.
 */
public interface INettyTransportProvider {
    /**
     * Create a new {@link EventLoopGroup} compatible with this transport type.
     *
     * @param config event loop group configuration
     * @return a created event loop group
     * @throws EventLoopGroupCreationException if any error occurs
     */
    EventLoopGroup createEventLoopGroup(IObject config)
            throws EventLoopGroupCreationException;

    /**
     * Get factory producing channels implementing required interface and supported by this transport.
     *
     * <p>
     *  Some of interfaces that <em>may</em> be supported by transport implementations are:
     *  <ul>
     *      <li>{@link io.netty.channel.socket.SocketChannel Socket channel}</li>
     *      <li>{@link io.netty.channel.socket.ServerSocketChannel Server socket channel}</li>
     *      <li>{@link io.netty.channel.socket.DatagramChannel Datagram channel}</li>
     *  </ul>
     * </p>
     *
     * @param channelType required channel interface
     * @param <T> type of required interface
     * @return channel factory
     * @throws InvalidArgumentException if {@code channelType} is not a interface
     * @throws UnsupportedChannelTypeException if {@code channelType} is not supported by this transport implementation
     */
    <T extends Channel> ChannelFactory<? extends T> getChannelFactory(Class<T> channelType)
            throws InvalidArgumentException, UnsupportedChannelTypeException;

    /**
     * Check if given event loop group is compatible with this transport.
     *
     * @param eventLoopGroup the event loop group to check
     * @throws InvalidEventLoopGroupException if the group is not compatible with this transport
     */
    void verifyEventLoopGroup(EventLoopGroup eventLoopGroup)
            throws InvalidEventLoopGroupException;
}
