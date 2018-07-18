package info.smart_tools.smartactors.endpoints_netty_plugins.endpoints_netty_plugin;

import info.smart_tools.smartactors.base.interfaces.i_addition_dependency_strategy.IAdditionDependencyStrategy;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.FunctionExecutionException;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.endpoint_components_netty.inetty_transport_provider.exceptions.InvalidEventLoopGroupException;
import info.smart_tools.smartactors.endpoint_components_netty.inetty_transport_provider.exceptions.UnsupportedChannelTypeException;
import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_pipeline_set.IEndpointPipelineSet;
import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_pipeline_set.exceptions.PipelineCreationException;
import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_pipeline_set.exceptions.PipelineDescriptionNotFoundException;
import info.smart_tools.smartactors.endpoints_netty.netty_tcp_single_port_server_endpoint.NettyTCPSinglePortServerEndpoint;
import info.smart_tools.smartactors.endpoints_netty.netty_ud_pserver_endpoint.NettyUDPServerEndpoint;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;

public class EndpointsNettyPlugin extends BootstrapPlugin {

    /**
     * The constructor.
     *
     * @param bootstrap    the bootstrap
     */
    public EndpointsNettyPlugin(final IBootstrap bootstrap) {
            super(bootstrap);
    }

    @Item("register_netty_endpoint_skeletons")
    public void registerSkeletons() throws Exception {
        IAdditionDependencyStrategy skeletonStorage
                = IOC.resolve(Keys.getOrAdd("expandable_strategy#create endpoint"));

        skeletonStorage.register("netty/server/tcp/single-port",
                new ApplyFunctionToArgumentsStrategy(args -> {
            try {
                return new NettyTCPSinglePortServerEndpoint(
                        (IObject) args[1],
                        (IEndpointPipelineSet) args[2]
                );
            } catch (ReadValueException | ResolutionException | InvalidEventLoopGroupException
                    | PipelineDescriptionNotFoundException | UnsupportedChannelTypeException
                    | PipelineCreationException e) {
                throw new FunctionExecutionException(e);
            }
        }));
        skeletonStorage.register("netty/udp/single-port",
                new ApplyFunctionToArgumentsStrategy(args -> {
            try {
                return new NettyUDPServerEndpoint(
                        (IObject) args[1],
                        (IEndpointPipelineSet) args[2]
                );
            } catch (ReadValueException | ResolutionException | UnsupportedChannelTypeException
                    | InvalidEventLoopGroupException | PipelineCreationException
                    | PipelineDescriptionNotFoundException e) {
                throw new FunctionExecutionException(e);
            }
        }));
    }
}
