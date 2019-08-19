package info.smart_tools.smartactors.message_processing.condition_chain_choice_strategy;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing.chain_call_receiver.IChainChoiceStrategy;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;

/**
 * {@link IChainChoiceStrategy Chain choice strategy} that returns chain id depending the chainCondition flag.
 */
public class ConditionChainChoiceStrategy implements IChainChoiceStrategy {
    private final IFieldName chainConditionFN;
    private final IFieldName trueChainFN;
    private final IFieldName falseChainFN;

    /**
     * Constructor
     * @throws ResolutionException sometimes
     */
    public ConditionChainChoiceStrategy() throws ResolutionException {
        chainConditionFN = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "chainCondition");
        trueChainFN = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "trueChain");
        falseChainFN = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "falseChain");
    }

    @Override
    public Object chooseChain(final IMessageProcessor messageProcessor)
            throws InvalidArgumentException, ReadValueException {
        IFieldName chainFN = ((Boolean) messageProcessor.getMessage().getValue(chainConditionFN) ? trueChainFN : falseChainFN);
        return messageProcessor.getSequence().getCurrentReceiverArguments().getValue(chainFN);
    }
}
