package info.smart_tools.smartactors.message_processing_plugins.starter_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;

/**
 * Implementation of {@link IPlugin}.
 *
 */
// TODO: Delete
public class PluginStarter implements IPlugin {

    /** Local storage for instance of {@link IBootstrap}*/
    private IBootstrap<IBootstrapItem<String>> bootstrap;

    /**
     * @param bootstrap Target bootstrap for adding strategy
     * @throws InvalidArgumentException if any errors occurred
     */
    public PluginStarter(final IBootstrap<IBootstrapItem<String>> bootstrap)
            throws InvalidArgumentException {
        if (null == bootstrap) {
            throw new InvalidArgumentException("Incoming argument should not be null.");
        }
        this.bootstrap = bootstrap;
    }

    @Override
    public void load()
            throws PluginException {
        try {
            IBootstrapItem<String> configureItem = new BootstrapItem("starter");
            configureItem
                    .before("read_initial_config")
                    .process(() -> {
//                        try {
//                            IConfigurationManager configurationManager = IOC.resolve(
//                                    Keys.getKeyByName(IConfigurationManager.class.getCanonicalName()));
//
//                            configurationManager.configure();
//                        } catch (ResolutionException | InvalidStateException | ConfigurationProcessingException e) {
//                            throw new ActionExecutionException(e);
//                        }
                    });

            bootstrap.add(configureItem);
        } catch (InvalidArgumentException e) {
            throw new PluginException(e);
        }
    }
}
