package info.smart_tools.smartactors.message_processing.chain_call_receiver;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageReceiver;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IReceiverChain;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.ChainChoiceException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.ChainNotFoundException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.MessageReceiveException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.NestedChainStackOverflowException;

/**
 * Receiver that calls {@link IReceiverChain} chosen by a {@link IChainChoiceStrategy} on a message.
 */
public class ChainCallReceiver implements IMessageReceiver {


    private IChainChoiceStrategy chainChoiceStrategy;
    private final IFieldName scopeSwitchingFieldName;

    /**
     * The constructor.
     *
     * @param chainChoiceStrategy           strategy to use
     * @throws InvalidArgumentException     if storage is {@code null}
     * @throws ResolutionException          if IOC is not initialized
     */
    public ChainCallReceiver(final IChainChoiceStrategy chainChoiceStrategy)
            throws InvalidArgumentException, ResolutionException {
        if (null == chainChoiceStrategy) {
            throw new InvalidArgumentException("Strategy should not be null.");
        }

        IKey iFieldNameStrategyKey = Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName");

        this.chainChoiceStrategy = chainChoiceStrategy;
        this.scopeSwitchingFieldName = IOC.resolve(iFieldNameStrategyKey, "scopeSwitching");
    }

    @Override
    public void receive(final IMessageProcessor processor)
            throws MessageReceiveException {
        try {
            Object chainName = chainChoiceStrategy.chooseChain(processor);
            Boolean scopeSwitching = (Boolean) processor.getSequence().getCurrentReceiverArguments().getValue(scopeSwitchingFieldName);
            if (scopeSwitching == null || scopeSwitching) {
                processor.getSequence().setScopeSwitchingChainName(chainName);
            }
            processor.getSequence().callChainSecurely(chainName, processor);
        } catch (ChainChoiceException | ChainNotFoundException | NestedChainStackOverflowException |
                ReadValueException | InvalidArgumentException e) {
            throw new MessageReceiveException("Could not call nested chain.", e);
        }
    }

    @Override
    public void dispose() {
    }
}
