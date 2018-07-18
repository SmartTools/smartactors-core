package info.smart_tools.smartactors.endpoint_components_generic_plugins.endpoint_pipeline_set_plugin;

import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.endpoint_components_generic.default_endpoint_pipeline_set.DefaultEndpointPipelineSet;
import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_profile.IEndpointProfile;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;

public class EndpointPipelineSetPlugin extends BootstrapPlugin {

    /**
     * The constructor.
     *
     * @param bootstrap    the bootstrap
     */
    public EndpointPipelineSetPlugin(final IBootstrap bootstrap) {
            super(bootstrap);
    }

    @Item("endpoint_pipeline_set_creation_strategy")
    public void registerCreationStrategy() throws Exception {
        IOC.register(Keys.getOrAdd("create endpoint pipeline set"),
                new ApplyFunctionToArgumentsStrategy(args -> {
                    IEndpointProfile profile = (IEndpointProfile) args[0];
                    IObject config = (IObject) args[1];

                    return new DefaultEndpointPipelineSet(config, profile);
                }));
    }
}
