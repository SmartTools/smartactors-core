package info.smart_tools.smartactors.endpoints_generic_plugins.endpoints_generic_plugin;

import info.smart_tools.smartactors.base.interfaces.i_addition_dependency_strategy.IAdditionDependencyStrategy;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.FunctionExecutionException;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_pipeline_set.IEndpointPipelineSet;
import info.smart_tools.smartactors.endpoints_generic.generic_outbound_endpoint.GenericOutboundEndpoint;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;

public class EndpointsGenericPlugin extends BootstrapPlugin {

    /**
     * The constructor.
     *
     * @param bootstrap    the bootstrap
     */
    public EndpointsGenericPlugin(final IBootstrap bootstrap) {
            super(bootstrap);
    }

    @Item("generic_outbound_endpoint")
    public void registerOutboundEndpoint() throws Exception {
        IAdditionDependencyStrategy skeletonStorage
                = IOC.resolve(Keys.getOrAdd("expandable_strategy#create endpoint"));

        skeletonStorage.register("generic/outbound",
                new ApplyFunctionToArgumentsStrategy(args -> {
                    try {
                        return new GenericOutboundEndpoint(
                                (IObject) args[1],
                                (IEndpointPipelineSet) args[2]
                        );
                    } catch (Exception e) {
                        throw new FunctionExecutionException(e);
                    }
                }));
    }
}
