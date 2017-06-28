package info.smart_tools.smartactors.message_processing_plugins.message_bus_response_strategy;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import info.smart_tools.smartactors.message_bus.message_bus_handler.MessageBusResponseStrategy;

public class MessageBusResponseStrategyPlugin  extends BootstrapPlugin {

    /**
     * The constructor.
     *
     * @param bootstrap    the bootstrap
     */
    public MessageBusResponseStrategyPlugin(final IBootstrap bootstrap) {
        super(bootstrap);
    }

    @Item("message_bus_response_strategy")
    @After({"null_response_strategy"})
    public void registerMessageBusResponseStrategy()
            throws ResolutionException, RegistrationException, InvalidArgumentException {
        IOC.register(Keys.getOrAdd("message bus response strategy"), new SingletonStrategy(new MessageBusResponseStrategy()));
    }
}