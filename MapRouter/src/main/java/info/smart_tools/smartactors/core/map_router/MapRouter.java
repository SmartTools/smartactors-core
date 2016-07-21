package info.smart_tools.smartactors.core.map_router;

import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.irouter.IRouter;
import info.smart_tools.smartactors.core.irouter.exceptions.RouteNotFoundException;
import info.smart_tools.smartactors.core.message_processing.IMessageReceiver;

import java.text.MessageFormat;
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
        map.put(targetId, receiver);
    }
}
