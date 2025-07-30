package info.smart_tools.smartactors.message_processing_plugins.null_response_strategy_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.ioc.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing.null_response_strategy.NullResponseStrategy;

public class NullResponseStrategyPlugin extends BootstrapPlugin {

    /**
     * The constructor.
     *
     * @param bootstrap    the bootstrap
     */
    public NullResponseStrategyPlugin(final IBootstrap bootstrap) {
            super(bootstrap);
    }

    @Item("null_response_strategy")
    @After({
            "IOC",
            "IFieldNamePlugin",
    })
    public void registerNullResponseStrategy()
            throws ResolutionException, RegistrationException, InvalidArgumentException {
        IOC.register(Keys.getKeyByName("null response strategy"), new SingletonStrategy(NullResponseStrategy.INSTANCE));
    }

    @ItemRevert("null_response_strategy")
    public void unregisterNullResponseStrategy() {
        String[] itemNames = { "null response strategy" };
        Keys.unregisterByNames(itemNames);
    }
}
