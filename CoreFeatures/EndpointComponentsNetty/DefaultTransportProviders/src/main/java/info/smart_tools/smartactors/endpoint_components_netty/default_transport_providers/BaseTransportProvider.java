package info.smart_tools.smartactors.endpoint_components_netty.default_transport_providers;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.endpoint_components_netty.inetty_transport_provider.INettyTransportProvider;
import info.smart_tools.smartactors.endpoint_components_netty.inetty_transport_provider.exceptions.InvalidEventLoopGroupException;
import info.smart_tools.smartactors.endpoint_components_netty.inetty_transport_provider.exceptions.UnsupportedChannelTypeException;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFactory;
import io.netty.channel.EventLoopGroup;

import java.util.HashMap;
import java.util.Map;

/**
 * Base class for simple transport providers.
 */
public abstract class BaseTransportProvider implements INettyTransportProvider {
    private final Map<Class<? extends Channel>, ChannelFactory<? extends Channel>> channelTypes = new HashMap<>();
    private final Class<? extends EventLoopGroup> eventLoopGroupClass;

    /**
     * The constructor.
     *
     * @param eventLoopGroupClass class of required event loop group
     */
    public BaseTransportProvider(final Class<? extends EventLoopGroup> eventLoopGroupClass) {
        this.eventLoopGroupClass = eventLoopGroupClass;
    }

    /**
     * @param channelType type of channel
     * @param factory     channel factory
     * @param <T>         type of channel provided by factory
     */
    public <T extends Channel> void registerChannelType(
            final Class<T> channelType,
            final ChannelFactory<? extends T> factory) {
        channelTypes.put(channelType, factory);
    }

    @Override
    public <T extends Channel> ChannelFactory<? extends T> getChannelFactory(final Class<T> channelType)
            throws InvalidArgumentException, UnsupportedChannelTypeException {
        if (!channelType.isInterface()) {
            throw new InvalidArgumentException("Channel type is not interface.");
        }

        ChannelFactory<? extends Channel> clz = channelTypes.get(channelType);

        if (null == clz) {
            throw new UnsupportedChannelTypeException("Unsupported channel type: " + channelType.getCanonicalName());
        }

        // Validity of this cast is guaranteed by #registerChannelType type constraints
        @SuppressWarnings({"unchecked"})
        ChannelFactory<? extends T> clz0 = (ChannelFactory<? extends T>) clz;

        return clz0;
    }

    @Override
    public void verifyEventLoopGroup(final EventLoopGroup eventLoopGroup)
            throws InvalidEventLoopGroupException {
        if (!eventLoopGroupClass.isInstance(eventLoopGroup)) {
            throw new InvalidEventLoopGroupException("Invalid event loop group class: " +
                    eventLoopGroup.getClass().getCanonicalName() +
                    " (expected subclass of " + eventLoopGroupClass.getCanonicalName() + ")");
        }
    }
}
