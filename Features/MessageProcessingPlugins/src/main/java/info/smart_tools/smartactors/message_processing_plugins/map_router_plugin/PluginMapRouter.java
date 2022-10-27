package info.smart_tools.smartactors.message_processing_plugins.map_router_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.ioc.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing.map_router.MapRouter;
import info.smart_tools.smartactors.message_processing_interfaces.irouter.IRouter;

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
                    .after("IOC")
                    .process(() -> {
                        try {
                            IOC.register(
                                    Keys.getKeyByName(IRouter.class.getCanonicalName()),
                                    new SingletonStrategy(new MapRouter(new ConcurrentHashMap<>())));
                        } catch (ResolutionException e) {
                            throw new ActionExecutionException("MapRouter plugin can't load: can't get MapRouter key", e);
                        } catch (InvalidArgumentException e) {
                            throw new ActionExecutionException("MapRouter plugin can't load: can't create strategy", e);
                        } catch (RegistrationException e) {
                            throw new ActionExecutionException("MapRouter plugin can't load: can't register new strategy", e);
                        }
                    })
                    .revertProcess(() -> {
                        String[] keyNames = { IRouter.class.getCanonicalName() };
                        Keys.unregisterByNames(keyNames);
                    });

            bootstrap.add(routerItem);
        } catch (InvalidArgumentException e) {
            throw new PluginException(e);
        }
    }
}
