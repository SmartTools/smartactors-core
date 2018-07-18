package info.smart_tools.smartactors.endpoint_components_generic_plugins.message_handler_resolution_strategies_plugin;

import info.smart_tools.smartactors.base.interfaces.i_addition_dependency_strategy.IAdditionDependencyStrategy;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.endpoint_components_generic.client_handlers.ErrorClientHandler;
import info.smart_tools.smartactors.endpoint_components_generic.client_handlers.StartClientHandler;
import info.smart_tools.smartactors.endpoint_components_generic.client_handlers.SuccessClientHandler;
import info.smart_tools.smartactors.endpoint_components_generic.outbound_url_parser.OutboundURLParser;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;

public class GenericClientMessageHandlersPlugin extends BootstrapPlugin {
    /**
     * The constructor.
     *
     * @param bootstrap the bootstrap
     */
    public GenericClientMessageHandlersPlugin(final IBootstrap bootstrap) {
        super(bootstrap);
    }

    @Item("generic_client_endpoint_message_handler")
    @After({
            "base_message_handler_strategies",
            "global_message_handler_tables_storage",
    })
    public void registerHandlers() throws Exception {
        IAdditionDependencyStrategy storage = IOC.resolve(Keys.getOrAdd("expandable_strategy#endpoint message handler"));

        storage.register("client callback/start",
                new SingletonStrategy(new StartClientHandler()));
        storage.register("client callback/success",
                new SingletonStrategy(new SuccessClientHandler()));
        storage.register("client callback/error",
                new SingletonStrategy(new ErrorClientHandler()));

        storage.register("outbound url parser",
                new SingletonStrategy(new OutboundURLParser()));
    }
}
