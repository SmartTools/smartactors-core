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
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
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
        sequenceDumpFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "sequenceDump");
        messageFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "message");
        responsibleCheckpointIdFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "responsibleCheckpointId");
        checkpointEntryIdFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "checkpointEntryId");
        prevCheckpointIdFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "prevCheckpointId");
        prevCheckpointEntryIdFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "prevCheckpointEntryId");
        checkpointStatusFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "checkpointStatus");
        entryIdFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "entryId");
    }

    @Override
    public void init(final IObject state, final IObject args, final IMessageProcessor processor)
            throws RecoverStrategyInitializationException {
        try {
            IObject sequenceDump = IOC.resolve(Keys.getKeyByName("make dump"), processor.getSequence(), args);

            state.setValue(sequenceDumpFieldName, sequenceDump);
        } catch (ResolutionException | ChangeValueException | InvalidArgumentException e) {
            throw new RecoverStrategyInitializationException("Error occurred preparing dump of message processing sequence.", e);
        }
    }

    @Override
    public void reSend(final IObject state) throws RecoverStrategyExecutionException {
        try {
            String sMessage = ((IObject) state.getValue(messageFieldName)).serialize();
            IObject messageClone = IOC.resolve(Keys.getKeyByName(IObject.class.getName()), sMessage);

            IObject checkpointStatus = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"));

            checkpointStatus.setValue(responsibleCheckpointIdFieldName, state.getValue(responsibleCheckpointIdFieldName));
            checkpointStatus.setValue(checkpointEntryIdFieldName, state.getValue(entryIdFieldName));
            checkpointStatus.setValue(prevCheckpointEntryIdFieldName, state.getValue(prevCheckpointEntryIdFieldName));
            checkpointStatus.setValue(prevCheckpointIdFieldName, state.getValue(prevCheckpointIdFieldName));

            messageClone.setValue(checkpointStatusFieldName, checkpointStatus);

            IMessageProcessingSequence sequence = IOC.resolve(Keys.getKeyByName("recover message processing sequence"),
                    state.getValue(sequenceDumpFieldName), messageClone);

            IMessageProcessor processor = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor"),
                    IOC.resolve(Keys.getKeyByName("task_queue")),
                    sequence
                    );

            processor.process(messageClone, IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject")));
        } catch (ReadValueException | InvalidArgumentException | SerializeException | ResolutionException | ChangeValueException
                | MessageProcessorProcessException e) {
            throw new RecoverStrategyExecutionException("Error occurred re-sending message.", e);
        }
    }
}
