package info.smart_tools.smartactors.endpoint_components_generic_plugins.endpoint_pipeline_plugin;

import info.smart_tools.smartactors.base.interfaces.iaction.IFunction0;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.endpoint_components_generic.default_endpoint_pipeline_implementation.PipelineParseStrategy;
import info.smart_tools.smartactors.endpoint_components_generic.default_message_context_implementation.DefaultMessageContextImplementation;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;

public class EndpointPipelinePlugin extends BootstrapPlugin {

    /**
     * The constructor.
     *
     * @param bootstrap    the bootstrap
     */
    public EndpointPipelinePlugin(final IBootstrap bootstrap) {
            super(bootstrap);
    }

    @Item("default_endpoint_message_context_factory")
    public void registerDefaultContextFactory() throws Exception {
        // TODO:: Implement a factory that will analyze handler parameter types and generate context class implementing required interfaces
        IFunction0 factory = DefaultMessageContextImplementation::new;
        IOC.register(Keys.getOrAdd("endpoint message context factory"),
                new SingletonStrategy(factory));
    }

    @Item("endpoint_pipeline_parse_strategy")
    @After({
            "default_endpoint_message_context_factory",
    })
    public void registerParseStrategy() throws Exception {
        IOC.register(Keys.getOrAdd("create endpoint pipeline"), new PipelineParseStrategy());
    }
}
