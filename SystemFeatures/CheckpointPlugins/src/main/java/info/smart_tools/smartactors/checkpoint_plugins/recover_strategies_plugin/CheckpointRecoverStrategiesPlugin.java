package info.smart_tools.smartactors.checkpoint_plugins.recover_strategies_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.checkpoint.recover_strategies.ReSendRestoringSequenceRecoverStrategy;
import info.smart_tools.smartactors.checkpoint.recover_strategies.ReSendToChainRecoverStrategy;
import info.smart_tools.smartactors.checkpoint.recover_strategies.chain_choice.ChainSequenceRecoverStrategy;
import info.smart_tools.smartactors.checkpoint.recover_strategies.chain_choice.SingleChainRecoverStrategy;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.ioc.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;

/**
 * Plugin that registers some recover strategies for checkpoint.
 */
public class CheckpointRecoverStrategiesPlugin extends BootstrapPlugin {
    /**
     * The constructor.
     *
     * @param bootstrap    the bootstrap
     */
    public CheckpointRecoverStrategiesPlugin(final IBootstrap bootstrap) {
        super(bootstrap);
    }

    /**
     * Registers recover strategy that sends message to the same chain every time.
     *
     * @throws ResolutionException if error occurs resolving key or dependencies
     * @throws RegistrationException if error occurs registering actor creation strategy
     * @throws InvalidArgumentException if some unexpected error occurs
     */
    @Item("checkpoint_recover_strategy:single_chain")
    @Before({"checkpoint_actor"})
    public void singleChainRecoverStrategyItem()
            throws ResolutionException, RegistrationException, InvalidArgumentException {
        IOC.register(Keys.getKeyByName("single chain recover strategy"),
                new SingletonStrategy(new ReSendToChainRecoverStrategy(new SingleChainRecoverStrategy())));
    }

    /**
     * Registers recover strategy that sends message to different chains depending on re-send trial number.
     *
     * @throws ResolutionException if error occurs resolving key or dependencies
     * @throws RegistrationException if error occurs registering actor creation strategy
     * @throws InvalidArgumentException if some unexpected error occurs
     */
    @Item("checkpoint_recover_strategy:chain_sequence")
    @Before({"checkpoint_actor"})
    public void chainSequenceRecoverStrategy()
            throws ResolutionException, RegistrationException, InvalidArgumentException {
        IOC.register(Keys.getKeyByName("chain sequence recover strategy"),
                new SingletonStrategy(new ReSendToChainRecoverStrategy(new ChainSequenceRecoverStrategy())));
    }

    /**
     * Registers recover strategy that re-sends message restoring message processing sequence state of original message.
     *
     * @throws ResolutionException if error occurs resolving key or dependencies
     * @throws RegistrationException if error occurs registering actor creation strategy
     * @throws InvalidArgumentException if some unexpected error occurs
     */
    @Item("checkpoint_recover_strategy:recover_sequence")
    @Before({"checkpoint_actor"})
    public void sequenceRecoverStrategy()
            throws ResolutionException, RegistrationException, InvalidArgumentException {
        IOC.register(Keys.getKeyByName("restore sequence recover strategy"),
                new SingletonStrategy(new ReSendRestoringSequenceRecoverStrategy()));
    }
}
