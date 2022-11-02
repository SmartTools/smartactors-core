package info.smart_tools.smartactors.message_processing_interfaces.irouter;

import info.smart_tools.smartactors.message_processing_interfaces.irouter.exceptions.RouteNotFoundException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageReceiver;

import java.util.List;

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

    /**
     * Remove a {@link IMessageReceiver} with given identifier.
     *
     * @param targetId    the target identifier
     */
    void unregister(Object targetId);

    /**
     * Enumerate receivers registered in this router.
     *
     * @return list of identifiers of all receivers registered in this router
     */
    List<Object> enumerate();
}
