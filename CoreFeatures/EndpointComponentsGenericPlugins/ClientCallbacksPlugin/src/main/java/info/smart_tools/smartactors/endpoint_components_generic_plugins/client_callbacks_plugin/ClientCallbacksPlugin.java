package info.smart_tools.smartactors.endpoint_components_generic_plugins.client_callbacks_plugin;

import info.smart_tools.smartactors.base.interfaces.i_addition_dependency_strategy.IAdditionDependencyStrategy;
import info.smart_tools.smartactors.base.simple_strict_storage_strategy.SimpleStrictStorageStrategy;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.endpoint_components_generic.interrupt_client_callback.InterruptClientCallback;
import info.smart_tools.smartactors.endpoint_components_generic.null_client_callback.NullClientCallback;
import info.smart_tools.smartactors.endpoint_components_generic.respond_to_chain_client_callback.RespondToChainClientCallback;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;

public class ClientCallbacksPlugin extends BootstrapPlugin {

    /**
     * The constructor.
     *
     * @param bootstrap    the bootstrap
     */
    public ClientCallbacksPlugin(final IBootstrap bootstrap) {
            super(bootstrap);
    }

    @Item("client_callbacks_storage")
    public void registerStorage() throws Exception {
        SimpleStrictStorageStrategy storage = new SimpleStrictStorageStrategy("client callback");
        IOC.register(Keys.getOrAdd("client callback"), storage);
        IOC.register(Keys.getOrAdd("expandable_strategy#client callback"),
                new SingletonStrategy(storage));
    }

    @Item("default_client_callback_types")
    @After({
            "client_callbacks_storage",
    })
    public void registerDefaultTypes() throws Exception {
        IAdditionDependencyStrategy storage = IOC.resolve(Keys.getOrAdd("expandable_strategy#client callback"));
        storage.register("interrupt", new SingletonStrategy(new InterruptClientCallback()));
        storage.register("respond-to-chain", new SingletonStrategy(new RespondToChainClientCallback()));
        storage.register("shoot-and-forget", new SingletonStrategy(new NullClientCallback()));
    }
}
