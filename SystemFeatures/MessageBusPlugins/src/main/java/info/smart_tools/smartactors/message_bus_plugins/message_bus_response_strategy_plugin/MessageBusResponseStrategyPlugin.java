package info.smart_tools.smartactors.message_bus_plugins.message_bus_response_strategy_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.ioc.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
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
    public void registerMessageBusResponseStrategy()
            throws ResolutionException, RegistrationException, InvalidArgumentException {
        IOC.register(Keys.getKeyByName("message bus response strategy"), new SingletonStrategy(new MessageBusResponseStrategy()));
    }
}
