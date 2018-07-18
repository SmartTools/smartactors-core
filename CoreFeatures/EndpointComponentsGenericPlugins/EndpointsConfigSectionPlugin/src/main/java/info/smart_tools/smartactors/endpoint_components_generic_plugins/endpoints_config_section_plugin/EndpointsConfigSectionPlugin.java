package info.smart_tools.smartactors.endpoint_components_generic_plugins.endpoints_config_section_plugin;

import info.smart_tools.smartactors.configuration_manager.interfaces.iconfiguration_manager.IConfigurationManager;
import info.smart_tools.smartactors.endpoint_components_generic.endpoints_config_section_strategy.EndpointsConfigSectionStrategy;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;

public class EndpointsConfigSectionPlugin extends BootstrapPlugin {

    /**
     * The constructor.
     *
     * @param bootstrap    the bootstrap
     */
    public EndpointsConfigSectionPlugin(final IBootstrap bootstrap) {
            super(bootstrap);
    }

    @Item("config_section:endpoint")
    @After({
            "named_endpoint_profile_storage",
            "endpoint_skeletons_storage",
            "endpoint_pipeline_set_creation_strategy",
    })
    public void registerSection() throws Exception {
        IConfigurationManager configurationManager = IOC.resolve(Keys.getOrAdd(IConfigurationManager.class.getCanonicalName()));
        configurationManager.addSectionStrategy(new EndpointsConfigSectionStrategy());
    }
}
