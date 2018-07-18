package info.smart_tools.smartactors.endpoint_components_netty.channel_pool_handlers;

import info.smart_tools.smartactors.endpoint_components_netty.isocket_connection_pool.ISocketConnectionPool;
import io.netty.util.AttributeKey;

/**
 * Static {@link AttributeKey} instances used by classes in this package.
 */
public enum AttributeKeys { ;
    /**
     * Key for attribute that stores a {@link ISocketConnectionPool} instance the channel should be returned to after
     * no longer needed.
     */
    public static final AttributeKey<ISocketConnectionPool> POOL_ATTRIBUTE_KEY = AttributeKey.valueOf("EP_CONNECTION_POOL");
}
