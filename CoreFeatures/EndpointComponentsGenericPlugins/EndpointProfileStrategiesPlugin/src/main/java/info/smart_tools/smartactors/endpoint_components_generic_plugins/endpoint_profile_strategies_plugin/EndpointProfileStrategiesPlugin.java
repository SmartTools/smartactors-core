package info.smart_tools.smartactors.endpoint_components_generic_plugins.endpoint_profile_strategies_plugin;

import info.smart_tools.smartactors.base.interfaces.iaction.exception.FunctionExecutionException;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.base.strategy.strategy_storage_strategy.StrategyStorageStrategy;
import info.smart_tools.smartactors.endpoint_components_generic.default_endpoint_profile.ProfileCreationStrategy;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;

import java.util.Map;

public class EndpointProfileStrategiesPlugin extends BootstrapPlugin {

    /**
     * The constructor.
     *
     * @param bootstrap    the bootstrap
     */
    public EndpointProfileStrategiesPlugin(final IBootstrap bootstrap) {
            super(bootstrap);
    }

    @Item("endpoint_profile_parse_strategy")
    @After({
            "named_endpoint_profile_storage",
    })
    public void registerParseStrategy() throws Exception {
        IOC.register(Keys.getOrAdd("parse endpoint profile"), new ProfileCreationStrategy());
    }

    @Item("named_endpoint_profile_storage")
    public void registerNamedStorage() throws Exception {
        StrategyStorageStrategy storage = new StrategyStorageStrategy(
                x -> x,
                (map, key) -> {
                    Object r = ((Map) map).get(key);
                    if (null == r) {
                        throw new FunctionExecutionException("No profile named '" + key + "' found.");
                    }
                    return r;
                }
        );

        IOC.register(Keys.getOrAdd("endpoint profile"), storage);
        IOC.register(Keys.getOrAdd("expandable_strategy#endpoint profile"),
                new SingletonStrategy(storage));
    }
}
