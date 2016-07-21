package info.smart_tools.smartactors.core.chain_call_receiver;

import info.smart_tools.smartactors.core.chain_call_receiver.exceptions.ChainChoiceException;
import info.smart_tools.smartactors.core.message_processing.IMessageProcessor;

/**
 * Strategy used by {@link ChainCallReceiver} to choose what chain to call.
 */
public interface IChainChoiceStrategy {
    /**
     * Choose a chain to be called on the message being processed by given message processor.
     *
     * @param messageProcessor    message processor processing the message
     * @return identifier of the chain to be called for the message
     * @throws ChainChoiceException when it is not possible to choose a chain to call
     */
    Object chooseChain(IMessageProcessor messageProcessor) throws ChainChoiceException;
}
