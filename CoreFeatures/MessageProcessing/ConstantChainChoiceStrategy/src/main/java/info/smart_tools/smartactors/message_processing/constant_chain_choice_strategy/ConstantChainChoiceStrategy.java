package info.smart_tools.smartactors.message_processing.constant_chain_choice_strategy;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing.chain_call_receiver.IChainChoiceStrategy;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;

/**
 * {@link IChainChoiceStrategy Chain choice strategy} that always returns the same chain id for the same step.
 */
public class ConstantChainChoiceStrategy implements IChainChoiceStrategy {
    private final IFieldName chainIdFieldName;

    public ConstantChainChoiceStrategy()
            throws ResolutionException {
        chainIdFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "chain");
    }

    @Override
    public Object chooseChain(final IMessageProcessor messageProcessor)
            throws InvalidArgumentException, ReadValueException {
        return messageProcessor.getSequence().getCurrentReceiverArguments().getValue(chainIdFieldName);
    }
}
