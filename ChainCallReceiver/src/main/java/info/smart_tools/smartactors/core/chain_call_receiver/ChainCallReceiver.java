package info.smart_tools.smartactors.core.chain_call_receiver;

import info.smart_tools.smartactors.core.chain_call_receiver.exceptions.ChainChoiceException;
import info.smart_tools.smartactors.core.ichain_storage.IChainStorage;
import info.smart_tools.smartactors.core.ichain_storage.exceptions.ChainNotFoundException;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.core.message_processing.IMessageReceiver;
import info.smart_tools.smartactors.core.message_processing.IReceiverChain;
import info.smart_tools.smartactors.core.message_processing.exceptions.MessageReceiveException;
import info.smart_tools.smartactors.core.message_processing.exceptions.NestedChainStackOverflowException;

/**
 * Receiver that calls {@link IReceiverChain} chosen by a {@link IChainChoiceStrategy} on a message.
 */
public class ChainCallReceiver implements IMessageReceiver {
    private IChainStorage chainStorage;
    private IChainChoiceStrategy chainChoiceStrategy;

    /**
     * The constructor.
     *
     * @param chainStorage           chains storage to use
     * @param chainChoiceStrategy    strategy to use
     * @throws InvalidArgumentException if storage is {@code null}
     * @throws InvalidArgumentException if strategy is {@code null}
     */
    public ChainCallReceiver(final IChainStorage chainStorage, final IChainChoiceStrategy chainChoiceStrategy)
            throws InvalidArgumentException {
        if (null == chainStorage) {
            throw new InvalidArgumentException("Storage should not be null.");
        }

        if (null == chainChoiceStrategy) {
            throw new InvalidArgumentException("Strategy should not be null.");
        }

        this.chainStorage = chainStorage;
        this.chainChoiceStrategy = chainChoiceStrategy;
    }

    @Override
    public void receive(final IMessageProcessor processor)
            throws MessageReceiveException {
        try {
            Object chainId = chainChoiceStrategy.chooseChain(processor);
            IReceiverChain chain = chainStorage.resolve(chainId);
            processor.getSequence().callChain(chain);
        } catch (ChainChoiceException | ChainNotFoundException | NestedChainStackOverflowException e) {
            throw new MessageReceiveException("Could not call nested chain.", e);
        }
    }
}
