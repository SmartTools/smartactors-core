package info.smart_tools.smartactors.plugin.map_router;

import info.smart_tools.smartactors.core.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.core.iaction.exception.ActionExecuteException;
import info.smart_tools.smartactors.core.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.core.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.core.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.iplugin.IPlugin;
import info.smart_tools.smartactors.core.iplugin.exception.PluginException;
import info.smart_tools.smartactors.core.irouter.IRouter;
import info.smart_tools.smartactors.core.map_router.MapRouter;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.core.singleton_strategy.SingletonStrategy;

import java.util.concurrent.ConcurrentHashMap;

/**
 *
 */
public class PluginMapRouter implements IPlugin {
    private final IBootstrap<IBootstrapItem<String>> bootstrap;

    /**
     * The constructor.
     *
     * @param bootstrap    the bootstrap
     */
    public PluginMapRouter(final IBootstrap<IBootstrapItem<String>> bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    public void load() throws PluginException {
        try {
            BootstrapItem routerItem = new BootstrapItem("router");

            routerItem
                    .after("ioc")
                    .process(() -> {
                        try {
                            IOC.register(
                                    Keys.getOrAdd(IRouter.class.getCanonicalName()),
                                    new SingletonStrategy(new MapRouter(new ConcurrentHashMap<>())));
                        } catch (InvalidArgumentException | ResolutionException | RegistrationException e) {
                            throw new ActionExecuteException(e);
                        }
                    });

            bootstrap.add(routerItem);
        } catch (InvalidArgumentException e) {
            throw new PluginException(e);
        }
    }
}