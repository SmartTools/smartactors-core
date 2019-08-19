package info.smart_tools.smartactors.version_management.versioned_router_decorator;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.class_management.interfaces.imodule.IModule;
import info.smart_tools.smartactors.class_management.module_manager.ModuleManager;
import info.smart_tools.smartactors.message_processing_interfaces.irouter.IRouter;
import info.smart_tools.smartactors.message_processing_interfaces.irouter.exceptions.RouteNotFoundException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageReceiver;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of {@link IRouter} that uses a {@link Map} instance.
 */
public class VersionedRouterDecorator implements IRouter {

    private final Map<Object, Map<IModule, Object>> map;
    private IRouter router;

    /**
     * The constructor.
     *
     * @param map    {@link Map} instance to use as routing table
     * @throws InvalidArgumentException if map is {@code null}
     */
    public VersionedRouterDecorator(final Map<Object, Map<IModule, Object>> map, final IRouter router)
            throws InvalidArgumentException {
        if (null == map || router == null) {
            throw new InvalidArgumentException("Map and router should not be null.");
        }

        this.map = map;
        this.router = router;
    }

    @Override
    public IMessageReceiver route(final Object targetId) throws RouteNotFoundException {
        Object receiverId = null;
        Map<IModule, Object> versions = map.get(targetId);

        if (versions != null) {
            receiverId = ModuleManager.getFromMap(versions);
        }

        if (null == receiverId) {
            throw new RouteNotFoundException(MessageFormat.format("Route to {0} not found.", targetId));
        }

        return router.route(receiverId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void register(final Object targetId, final IMessageReceiver receiver) {
        Map<IModule, Object> versions = map.get(targetId);

        if (versions == null) {
            try {
                versions = map.getClass().newInstance();
            } catch (IllegalAccessException | InstantiationException e) {
                versions = new ConcurrentHashMap<>();
            }
            map.put(targetId, versions);
        }

        IModule currentModule = ModuleManager.getCurrentModule();
        for (IModule module : versions.keySet()) {
            if (!currentModule.getName().equals(module.getName())) {
                System.out.println(
                        "[WARNING] Receiver with Id '" + targetId.toString() +
                        "' already registered in module '" + module.getName() + ":" + module.getVersion() +
                        "', but registering in module '" + currentModule.getName() + ":" + currentModule.getVersion() + "'."
                );
            }
        }

        Object receiverId = targetId.toString() + ":" + currentModule.getId().toString();
        versions.put(currentModule, receiverId);

        router.register(receiverId, receiver);
    }

    @Override
    public void unregister(final Object targetId) {
        Object receiverId = null;
        Map<IModule, Object> versions = map.get(targetId);

        if (versions != null) {
            receiverId = versions.remove(ModuleManager.getCurrentModule());
            if (versions.size() == 0) {
                map.remove(targetId);
            }
        }

        router.unregister(receiverId);
    }

    @Override
    public List<Object> enumerate() {
        return new ArrayList<>(map.keySet());
    }
}
