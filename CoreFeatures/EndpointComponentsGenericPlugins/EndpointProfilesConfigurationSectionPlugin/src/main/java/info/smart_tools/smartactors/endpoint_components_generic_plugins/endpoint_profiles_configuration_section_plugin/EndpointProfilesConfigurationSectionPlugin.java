package info.smart_tools.smartactors.endpoint_components_generic_plugins.endpoint_profiles_configuration_section_plugin;

import info.smart_tools.smartactors.configuration_manager.interfaces.iconfiguration_manager.IConfigurationManager;
import info.smart_tools.smartactors.endpoint_components_generic.endpoint_profiles_configuration_section.EndpointProfilesConfigurationSectionStrategy;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;

/**
 * Plugin that registers {@code "endpointProfiles"} configuration section strategy.
 */
public class EndpointProfilesConfigurationSectionPlugin extends BootstrapPlugin {

    /**
     * The constructor.
     *
     * @param bootstrap    the bootstrap
     */
    public EndpointProfilesConfigurationSectionPlugin(final IBootstrap bootstrap) {
            super(bootstrap);
    }

    @Item("config_section:endpointProfiles")
    @Before({
            "config_section:endpoint"
    })
    public void registerSection() throws Exception {
        IConfigurationManager configurationManager = IOC.resolve(
                Keys.getOrAdd(IConfigurationManager.class.getCanonicalName()));
        configurationManager.addSectionStrategy(new EndpointProfilesConfigurationSectionStrategy());
    }
}
