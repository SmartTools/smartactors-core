package info.smart_tools.smartactors.checkpoint.recover_strategies;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.checkpoint.interfaces.IRecoverStrategy;
import info.smart_tools.smartactors.checkpoint.interfaces.exceptions.RecoverStrategyExecutionException;
import info.smart_tools.smartactors.checkpoint.interfaces.exceptions.RecoverStrategyInitializationException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.SerializeException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessingSequence;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.MessageProcessorProcessException;

/**
 * {@link IRecoverStrategy Recover strategy} that re-sends the message restoring state of {@link IMessageProcessingSequence processing
 * sequence} of original message.
 */
public class ReSendRestoringSequenceRecoverStrategy implements IRecoverStrategy {
    private final IFieldName sequenceDumpFieldName;
    private final IFieldName messageFieldName;
    private final IFieldName responsibleCheckpointIdFieldName;
    private final IFieldName checkpointEntryIdFieldName;
    private final IFieldName prevCheckpointEntryIdFieldName;
    private final IFieldName entryIdFieldName;
    private final IFieldName prevCheckpointIdFieldName;
    private final IFieldName checkpointStatusFieldName;

    /**
     * The constructor.
     *
     * @throws ResolutionException if error occurs resolving dependencies
     */
    public ReSendRestoringSequenceRecoverStrategy()
            throws ResolutionException {
        sequenceDumpFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "sequenceDump");
        messageFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "message");
        responsibleCheckpointIdFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "responsibleCheckpointId");
        checkpointEntryIdFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "checkpointEntryId");
        prevCheckpointIdFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "prevCheckpointId");
        prevCheckpointEntryIdFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "prevCheckpointEntryId");
        checkpointStatusFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "checkpointStatus");
        entryIdFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "entryId");
    }

    @Override
    public void init(final IObject state, final IObject args, final IMessageProcessor processor)
            throws RecoverStrategyInitializationException {
        try {
            IObject sequenceDump = IOC.resolve(Keys.getOrAdd("make dump"), processor.getSequence(), args);

            state.setValue(sequenceDumpFieldName, sequenceDump);
        } catch (ResolutionException | ChangeValueException | InvalidArgumentException e) {
            throw new RecoverStrategyInitializationException("Error occurred preparing dump of message processing sequence.", e);
        }
    }

    @Override
    public void reSend(final IObject state) throws RecoverStrategyExecutionException {
        try {
            String sMessage = ((IObject) state.getValue(messageFieldName)).serialize();
            IObject messageClone = IOC.resolve(Keys.getOrAdd(IObject.class.getName()), sMessage);

            IObject checkpointStatus = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()));

            checkpointStatus.setValue(responsibleCheckpointIdFieldName, state.getValue(responsibleCheckpointIdFieldName));
            checkpointStatus.setValue(checkpointEntryIdFieldName, state.getValue(entryIdFieldName));
            checkpointStatus.setValue(prevCheckpointEntryIdFieldName, state.getValue(prevCheckpointEntryIdFieldName));
            checkpointStatus.setValue(prevCheckpointIdFieldName, state.getValue(prevCheckpointIdFieldName));

            messageClone.setValue(checkpointStatusFieldName, checkpointStatus);

            IMessageProcessingSequence sequence = IOC.resolve(Keys.getOrAdd("recover message processing sequence"),
                    state.getValue(sequenceDumpFieldName));

            IMessageProcessor processor = IOC.resolve(Keys.getOrAdd(IMessageProcessor.class.getCanonicalName()),
                    IOC.resolve(Keys.getOrAdd("task_queue")),
                    sequence
                    );

            processor.process(messageClone, IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName())));
        } catch (ReadValueException | InvalidArgumentException | SerializeException | ResolutionException | ChangeValueException
                | MessageProcessorProcessException e) {
            throw new RecoverStrategyExecutionException("Error occurred re-sending message.", e);
        }
    }
}
