package info.smart_tools.smartactors.feature_loader_plugins.filesystem_facade_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.feature_loader.filesystem_facade.FilesystemFacadeImpl;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.ioc.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;

/**
 * Plugin registering filesystem facade singleton in IOC.
 */
public class PluginFilesystemFacade implements IPlugin {
    private final IBootstrap<IBootstrapItem<String>> bootstrap;

    /**
     * Constructor
     * @param bootstrap the bootstrap
     */
    public PluginFilesystemFacade(final IBootstrap<IBootstrapItem<String>> bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    public void load() throws PluginException {
        try {
            IBootstrapItem<String> item = new BootstrapItem("filesystem_facade");

            item
                    .after("IOC")
                    .process(() -> {
                        try {
                            IOC.register(Keys.getKeyByName("filesystem facade"), new SingletonStrategy(new FilesystemFacadeImpl()));
                        } catch (ResolutionException | InvalidArgumentException | RegistrationException e) {
                            throw new ActionExecutionException(e);
                        }
                    });

            bootstrap.add(item);
        } catch (InvalidArgumentException e) {
            throw new PluginException(e);
        }
    }
}
