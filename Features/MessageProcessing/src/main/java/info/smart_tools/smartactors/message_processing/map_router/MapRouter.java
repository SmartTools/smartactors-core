package info.smart_tools.smartactors.message_processing.map_router;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.message_processing_interfaces.irouter.IRouter;
import info.smart_tools.smartactors.message_processing_interfaces.irouter.exceptions.RouteNotFoundException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageReceiver;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Implementation of {@link IRouter} that uses a {@link Map} instance.
 */
public class MapRouter implements IRouter {
    private final Map<Object, IMessageReceiver> map;

    /**
     * The constructor.
     *
     * @param map    {@link Map} instance to use as routing table
     * @throws InvalidArgumentException if map is {@code null}
     */
    public MapRouter(final Map<Object, IMessageReceiver> map)
            throws InvalidArgumentException {
        if (null == map) {
            throw new InvalidArgumentException("Map should not be null.");
        }

        this.map = map;
    }

    @Override
    public IMessageReceiver route(final Object targetId) throws RouteNotFoundException {
        IMessageReceiver receiver = map.get(targetId);

        if (null == receiver) {
            throw new RouteNotFoundException(MessageFormat.format("Route to {0} not found.", targetId));
        }

        return receiver;
    }

    @Override
    public void register(final Object targetId, final IMessageReceiver receiver) {
        IMessageReceiver oldReceiver = map.put(targetId, receiver);

        if (null != oldReceiver) {
            System.out.println(MessageFormat.format("[WARNING] replacing receiver ({0}) registered as ''{1}'' by {2}",
                    oldReceiver.toString(), targetId.toString(), receiver.toString()));
        }
    }

    @Override
    public void unregister(final Object targetId) {
        IMessageReceiver receiver = map.remove(targetId);

        if (null != receiver) {
            receiver.dispose();
        } else {
            System.out.println(MessageFormat.format("[WARNING] ''{0}'' has no receivers, nothing to delete",
                    targetId.toString()));
        }
    }

    @Override
    public List<Object> enumerate() {
        return new ArrayList<>(map.keySet());
    }
}
