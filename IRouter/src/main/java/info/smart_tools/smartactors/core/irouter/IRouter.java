package info.smart_tools.smartactors.core.irouter;

import info.smart_tools.smartactors.core.irouter.exceptions.RouteNotFoundException;
import info.smart_tools.smartactors.core.message_processing.IMessageReceiver;

/**
 * The router stores {@link IMessageReceiver}s.
 */
public interface IRouter {
    /**
     * Find a receiver associated with given identifier.
     *
     * @param targetId    identifier of the receiver
     * @return the receiver associated with the given identifier
     * @throws RouteNotFoundException if no receiver found for given identifier
     */
    IMessageReceiver route(Object targetId) throws RouteNotFoundException;

    /**
     * Store a {@link IMessageReceiver} as associated with given identifier.
     * 
     * @param targetId    the target identifier
     * @param receiver    e receiver
     */
    void register(Object targetId, IMessageReceiver receiver);
}
