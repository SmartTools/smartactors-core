package info.smart_tools.smartactors.message_processing.message_processing_sequence.dump_recovery;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.interfaces.istrategy.exception.StrategyException;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.message_processing.message_processing_sequence.MessageProcessingSequence;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessingSequence;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.ChainNotFoundException;

/**
 * IOC strategy that recovers a {@link MessageProcessingSequence} from {@link IObject} created by call of {@link
 * MessageProcessingSequence#dump(IObject) dump()} method of original sequence.
 */
public class MessageProcessingSequenceRecoveryStrategy implements IStrategy {

    @Override
    public <T> T resolve(final Object... args) throws StrategyException {
        try {
            IObject dump = (IObject) args[0];
            IObject message = (IObject) args[1];

            IMessageProcessingSequence sequence = new MessageProcessingSequence(dump, message);

            return (T) sequence;
        } catch (ResolutionException | ReadValueException | InvalidArgumentException |
                ClassCastException | ChainNotFoundException e) {
            throw new StrategyException(e);
        }
    }
}
