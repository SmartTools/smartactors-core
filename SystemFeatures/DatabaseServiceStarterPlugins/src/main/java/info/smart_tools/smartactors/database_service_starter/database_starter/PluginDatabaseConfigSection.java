package info.smart_tools.smartactors.database_service_starter.database_starter;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.configuration_manager.interfaces.iconfiguration_manager.IConfigurationManager;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;

public class PluginDatabaseConfigSection extends BootstrapPlugin {

    /**
     * The constructor.
     *
     * @param bootstrap    the bootstrap
     */
    public PluginDatabaseConfigSection(final IBootstrap bootstrap) {
        super(bootstrap);
    }

    @Item("config_section:database")
//    @Before({"config_section:objects"})
    public void registerCanonizationStrategies()
            throws ResolutionException, InvalidArgumentException {
        IConfigurationManager configurationManager =
                IOC.resolve(Keys.getKeyByName(IConfigurationManager.class.getCanonicalName()));
        configurationManager.addSectionStrategy(new DatabaseSectionStrategy());
    }
}
