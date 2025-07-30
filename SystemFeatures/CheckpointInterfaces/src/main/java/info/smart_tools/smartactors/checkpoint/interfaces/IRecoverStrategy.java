package info.smart_tools.smartactors.checkpoint.interfaces;

import info.smart_tools.smartactors.checkpoint.interfaces.exceptions.RecoverStrategyExecutionException;
import info.smart_tools.smartactors.checkpoint.interfaces.exceptions.RecoverStrategyInitializationException;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;

/**
 * Interface for a strategy re-sending a message.
 */
public interface IRecoverStrategy {
    /**
     * Prepare checkpoint entry for execution of this recover strategy.
     *
     * @param state        state of checkpoint entry
     * @param args         recover strategy configuration
     * @param processor    message processor processing the original message
     * @throws RecoverStrategyInitializationException if any error occurs
     */
    void init(IObject state, IObject args, IMessageProcessor processor) throws RecoverStrategyInitializationException;

    /**
     * Re-send message.
     *
     * @param state    state of the checkpoint entry associated with the message
     * @throws RecoverStrategyExecutionException if eny error occurs
     */
    void reSend(IObject state) throws RecoverStrategyExecutionException;
}
