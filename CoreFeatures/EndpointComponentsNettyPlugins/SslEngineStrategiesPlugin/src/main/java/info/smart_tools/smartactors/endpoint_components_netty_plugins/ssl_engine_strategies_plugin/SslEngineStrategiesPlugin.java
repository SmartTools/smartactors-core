package info.smart_tools.smartactors.endpoint_components_netty_plugins.ssl_engine_strategies_plugin;

import info.smart_tools.smartactors.endpoint_components_netty.ssl_channel_initialization_handler.ClientSSLContextResolutionStrategy;
import info.smart_tools.smartactors.endpoint_components_netty.ssl_channel_initialization_handler.ServerSSLContextResolutionStrategy;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;

public class SslEngineStrategiesPlugin extends BootstrapPlugin {

    /**
     * The constructor.
     *
     * @param bootstrap    the bootstrap
     */
    public SslEngineStrategiesPlugin(final IBootstrap bootstrap) {
            super(bootstrap);
    }

    @Item("netty_ssl_engine_strategies")
    public void registerStrategies() throws Exception {
        IOC.register(Keys.getOrAdd("netty server endpoint ssl context"),
                new ServerSSLContextResolutionStrategy());
        IOC.register(Keys.getOrAdd("netty client endpoint ssl context"),
                new ClientSSLContextResolutionStrategy());
    }
}
