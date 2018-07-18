package info.smart_tools.smartactors.endpoint_components_netty.default_channel_initialization_handlers;

import io.netty.util.AttributeKey;

/**
 * Class containing some instances of {@link AttributeKey netty attribute keys} used by components in this package.
 */
public enum ChannelAttributes { ;
    /**
     * Attribute containing identifier of attached outbound channel.
     */
    public static final AttributeKey<Object> OUTBOUND_CHANNEL_ID_KEY = AttributeKey.valueOf(Object.class, "OUTBOUND_CHANNEL_ID");
}
