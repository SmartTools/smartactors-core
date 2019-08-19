package info.smart_tools.smartactors.message_processing_plugins.constant_chain_choice_strategy_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing.constant_chain_choice_strategy.ConstantChainChoiceStrategy;

/**
 * Plugin that registers constant chain choice strategy.
 */
public class PluginConstantChainChoiceStrategy extends BootstrapPlugin {
    public PluginConstantChainChoiceStrategy(final IBootstrap bootstrap) {
        super(bootstrap);
    }

    /**
     * Register constant chain choice strategy as a singleton.
     *
     * @throws ResolutionException if error occurs resoling key or dependencies of chain choice strategy
     * @throws RegistrationException if error occurs registering the strategy
     * @throws InvalidArgumentException if {@link SingletonStrategy} doesn't like our arguments
     */
    @Item("constant_chain_choice_strategy")
    @After({"IOC", "IFieldNamePlugin"})
    @Before({"ChainCallReceiver"})
    public void item()
            throws ResolutionException, RegistrationException, InvalidArgumentException {
        IOC.register(Keys.getKeyByName("constant chain choice strategy"), new SingletonStrategy(new ConstantChainChoiceStrategy()));
    }

    /**
     * Unregisters constant chain choice strategy.
     */
    @ItemRevert("constant_chain_choice_strategy")
    public void revertItem() {
        String[] keyNames = { "constant chain choice strategy" };
        Keys.unregisterByNames(keyNames);
    }
}
