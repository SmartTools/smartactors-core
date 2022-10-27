package info.smart_tools.smartactors.message_processing_plugins.condition_chain_choice_strategy_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.ioc.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing.condition_chain_choice_strategy.ConditionChainChoiceStrategy;

/**
 * Plugin that registers condition chain choice strategy.
 */
public class PluginConditionChainChoiceStrategy extends BootstrapPlugin {

    /**
     * Constructor
     * @param bootstrap the bootstrap item
     */
    public PluginConditionChainChoiceStrategy(final IBootstrap bootstrap) {
        super(bootstrap);
    }

    /**
     * Register condition chain choice strategy as a singleton.
     *
     * @throws ResolutionException if error occurs resoling key or dependencies of chain choice strategy
     * @throws RegistrationException if error occurs registering the strategy
     * @throws InvalidArgumentException if {@link SingletonStrategy} doesn't like our arguments
     */
    @Item("condition_chain_choice_strategy")
    @After({
            "IOC",
            "IFieldNamePlugin",
    })
    @Before({
            "ChainCallReceiver",
    })
    public void item()
            throws ResolutionException, RegistrationException, InvalidArgumentException {
        IOC.register(Keys.getKeyByName("condition chain choice strategy"), new SingletonStrategy(new ConditionChainChoiceStrategy()));
    }

    /**
     * Unregisters condition chain choice strategy.
     */
    @ItemRevert("condition_chain_choice_strategy")
    public void revertItem() {
        String[] keyNames = { "condition chain choice strategy" };
        Keys.unregisterByNames(keyNames);
    }
}
