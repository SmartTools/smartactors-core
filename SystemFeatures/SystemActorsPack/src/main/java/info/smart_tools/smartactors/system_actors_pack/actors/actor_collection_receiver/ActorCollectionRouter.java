package info.smart_tools.smartactors.system_actors_pack.actors.actor_collection_receiver;

import info.smart_tools.smartactors.message_processing_interfaces.irouter.IRouter;
import info.smart_tools.smartactors.message_processing_interfaces.irouter.exceptions.RouteNotFoundException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageReceiver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActorCollectionRouter implements IRouter {

    private Map<Object, IMessageReceiver> storage = new HashMap<>();

    @Override
    public IMessageReceiver route(final Object targetId)
        throws RouteNotFoundException {
        return this.storage.get(targetId);
    }

    @Override
    public void register(final Object targetId, final IMessageReceiver receiver) {
        this.storage.put(targetId, receiver);
    }

    @Override
    public void unregister(Object targetId) {
        // TODO: implement
    }

    @Override
    public List<Object> enumerate() {
        return new ArrayList<>(storage.keySet());
    }
}
