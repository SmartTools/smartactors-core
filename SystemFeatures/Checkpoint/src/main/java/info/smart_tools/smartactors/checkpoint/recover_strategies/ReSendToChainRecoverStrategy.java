package info.smart_tools.smartactors.checkpoint.recover_strategies;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.checkpoint.interfaces.IRecoverStrategy;
import info.smart_tools.smartactors.checkpoint.interfaces.exceptions.RecoverStrategyExecutionException;
import info.smart_tools.smartactors.checkpoint.interfaces.exceptions.RecoverStrategyInitializationException;
import info.smart_tools.smartactors.checkpoint.recover_strategies.chain_choice.IRecoveryChainChoiceStrategy;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.SerializeException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_bus.interfaces.imessage_bus_container.exception.SendingMessageException;
import info.smart_tools.smartactors.message_bus.message_bus.MessageBus;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;

/**
 * {@link IRecoverStrategy Recover strategy} that re-sends message to chain chosen by a {@link IRecoveryChainChoiceStrategy strategy}.
 */
public class ReSendToChainRecoverStrategy implements IRecoverStrategy {
    private final IRecoveryChainChoiceStrategy chainChoiceStrategy;
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
     * @param chainChoiceStrategy    strategy to use to choose chain
     * @throws ResolutionException if error occurs resolving dependencies
     */
    public ReSendToChainRecoverStrategy(final IRecoveryChainChoiceStrategy chainChoiceStrategy)
            throws ResolutionException {
        this.chainChoiceStrategy = chainChoiceStrategy;

        responsibleCheckpointIdFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "responsibleCheckpointId");
        checkpointEntryIdFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "checkpointEntryId");
        prevCheckpointIdFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "prevCheckpointId");
        prevCheckpointEntryIdFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "prevCheckpointEntryId");
        checkpointStatusFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "checkpointStatus");
        entryIdFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "entryId");
        messageFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "message");
    }

    @Override
    public void init(final IObject state, final IObject args, final IMessageProcessor processor)
            throws RecoverStrategyInitializationException {
        chainChoiceStrategy.init(state, args);
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

            MessageBus.send(messageClone, chainChoiceStrategy.chooseRecoveryChain(state));
        } catch (ResolutionException | ReadValueException | InvalidArgumentException | ChangeValueException | SendingMessageException
                | SerializeException e) {
            throw new RecoverStrategyExecutionException("Error occurred re-sending a message.", e);
        }
    }
}
