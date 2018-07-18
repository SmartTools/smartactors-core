package info.smart_tools.smartactors.endpoint_components_generic_plugins.endpoint_skeletons_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.simple_strict_storage_strategy.SimpleStrictStorageStrategy;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.base.strategy.strategy_storage_strategy.StrategyStorageStrategy;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;

import java.util.Map;

public class EndpointSkeletonsPlugin extends BootstrapPlugin {

    /**
     * The constructor.
     *
     * @param bootstrap    the bootstrap
     */
    public EndpointSkeletonsPlugin(final IBootstrap bootstrap) {
            super(bootstrap);
    }

    @Item("endpoint_skeletons_storage")
    public void registerStorage() throws Exception {
        SimpleStrictStorageStrategy storage = new SimpleStrictStorageStrategy("endpoint skeleton");

        /*
         * (String skeletonId, IObject endpointConf, IEndpointPipelineSet pipelineSet) -> Object
         */
        IOC.register(Keys.getOrAdd("create endpoint"), storage);
        IOC.register(Keys.getOrAdd("expandable_strategy#create endpoint"),
                new SingletonStrategy(storage));
    }
}
