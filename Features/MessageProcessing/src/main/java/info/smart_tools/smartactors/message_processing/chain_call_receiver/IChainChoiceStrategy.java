package info.smart_tools.smartactors.message_processing.chain_call_receiver;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;

/**
 * Strategy used by {@link ChainCallReceiver} to choose what chain to call.
 */
public interface IChainChoiceStrategy {
    /**
     * @param messageProcessor    message processor processing the message
     *
     * @param messageProcessor message processor processing the message
     * @return name of the chain to be called for the message
     * @throws InvalidArgumentException if an error occurred on an instance of {@link IMessageProcessor} validation
     * @throws ReadValueException if an error occurred on reading a field of an instance of {@link info.smart_tools.smartactors.iobject.iobject.IObject}
     */
    Object chooseChain(IMessageProcessor messageProcessor)
            throws InvalidArgumentException, ReadValueException;
}
