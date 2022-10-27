package info.smart_tools.smartactors.checkpoint.recover_strategies.chain_choice;

import info.smart_tools.smartactors.checkpoint.interfaces.exceptions.RecoverStrategyExecutionException;
import info.smart_tools.smartactors.checkpoint.interfaces.exceptions.RecoverStrategyInitializationException;
import info.smart_tools.smartactors.iobject.iobject.IObject;

/**
 * Interface for a strategy choosing what chain to use to re-send a message.
 */
public interface IRecoveryChainChoiceStrategy {
    /**
     * Prepare checkpoint entry for execution of this recover strategy.
     *
     * @param state    state of checkpoint entry
     * @param args     recover strategy configuration
     * @throws RecoverStrategyInitializationException if any error occurs
     */
    void init(IObject state, IObject args) throws RecoverStrategyInitializationException;

    /**
     * Choose a receiver chain to use to re-send the message.
     *
     * @param state    state of the checkpoint entry
     * @return identifier of the chain to use
     * @throws RecoverStrategyExecutionException if any error occurs
     */
    Object chooseRecoveryChain(IObject state) throws RecoverStrategyExecutionException;
}
