package info.smart_tools.smartactors.endpoint_components_netty.client_context_binding_handlers;

import info.smart_tools.smartactors.iobject.iobject.IObject;
import io.netty.util.AttributeKey;

/**
 * {@link AttributeKey} instances used by classes in this package.
 */
public enum AttributeKeys { ;
    /**
     * Key for attribute that stores a request bound to a channel.
     */
    public static final AttributeKey<IObject> REQUEST_ATTRIBUTE_KEY = AttributeKey.valueOf("REQUEST");
}
