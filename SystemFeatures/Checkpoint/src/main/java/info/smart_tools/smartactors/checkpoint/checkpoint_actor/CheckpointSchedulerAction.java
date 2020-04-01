package info.smart_tools.smartactors.checkpoint.checkpoint_actor;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.checkpoint.interfaces.IRecoverStrategy;
import info.smart_tools.smartactors.checkpoint.interfaces.exceptions.RecoverStrategyExecutionException;
import info.smart_tools.smartactors.checkpoint.interfaces.exceptions.RecoverStrategyInitializationException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerAction;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerEntry;
import info.smart_tools.smartactors.scheduler.interfaces.exceptions.SchedulerActionExecutionException;
import info.smart_tools.smartactors.scheduler.interfaces.exceptions.SchedulerActionInitializationException;

/**
 * Action executed when checkpoint entry fires.
 */
public class CheckpointSchedulerAction implements ISchedulerAction {
    private final IFieldName recoverFieldName;
    private final IFieldName strategyFieldName;
    private final IFieldName recoverStrategyFieldName;
    private final IFieldName messageFieldName;
    private final IFieldName completedFieldName;
    private final IFieldName gotFeedbackFieldName;

    private final IFieldName responsibleCheckpointIdFieldName;
    private final IFieldName prevCheckpointEntryIdFieldName;
    private final IFieldName prevCheckpointIdFieldName;
    private final IFieldName processorFieldName;

    /**
     * The constructor.
     *
     * @throws ResolutionException if error occurs resolving any dependencies
     */
    public CheckpointSchedulerAction()
            throws ResolutionException {
        recoverFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "recover");
        strategyFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "strategy");
        recoverStrategyFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "recoverStrategy");
        messageFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "message");
        completedFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "completed");
        gotFeedbackFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "gotFeedback");

        responsibleCheckpointIdFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "responsibleCheckpointId");
        prevCheckpointIdFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "prevCheckpointId");
        prevCheckpointEntryIdFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "prevCheckpointEntryId");
        processorFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "processor");
    }

    @Override
    public void init(final ISchedulerEntry entry, final IObject args)
            throws SchedulerActionInitializationException {
        try {
            IObject recoverConfig = (IObject) args.getValue(recoverFieldName);
            String recoverStrategyId = (String) recoverConfig.getValue(strategyFieldName);
            IObject message = (IObject) args.getValue(messageFieldName);

            if (null == message) {
                throw new SchedulerActionInitializationException(
                        "Checkpoint scheduler action arguments should contain message object.", null);
            }

            entry.getState().setValue(messageFieldName, message);

            IRecoverStrategy strategy = IOC.resolve(Keys.getKeyByName(recoverStrategyId));

            strategy.init(entry.getState(), recoverConfig, (IMessageProcessor) args.getValue(processorFieldName));

            entry.getState().setValue(recoverStrategyFieldName, recoverStrategyId);

            // Store id's of currently and previously responsible for the message checkpoints and identifier of the message in storage of
            // previous checkpoint in the entry state.
            entry.getState().setValue(responsibleCheckpointIdFieldName, args.getValue(responsibleCheckpointIdFieldName));
            entry.getState().setValue(prevCheckpointIdFieldName, args.getValue(prevCheckpointIdFieldName));
            entry.getState().setValue(prevCheckpointEntryIdFieldName, args.getValue(prevCheckpointEntryIdFieldName));
        } catch (ReadValueException | InvalidArgumentException | ResolutionException | RecoverStrategyInitializationException
                | ChangeValueException e) {
            throw new SchedulerActionInitializationException("Error occurred initializing checkpoint action.", e);
        }
    }

    @Override
    public void execute(final ISchedulerEntry entry) throws SchedulerActionExecutionException {
        try {
            // Scheduling strategy or checkpoint actor may write a non-null value into "completed" field of entry state if the message
            // should be no more re-sent.
            // The entry will still be kept in both remote and local storage to avoid duplication of the message until it will be deleted.
            if (null != entry.getState().getValue(completedFieldName)) {
                // Checkpoint actor writes non-null value into "gotFeedback" field of the entry state when it gets a feedback from next
                // checkpoint.
                // If there was no feedback (the message did not reach next checkpoint before it ran out of re-send trials) then we should
                // execute a "failure action" that will handle the "lost" message.
                if (null == entry.getState().getValue(gotFeedbackFieldName)) {
                    IAction<IObject> failureAction = IOC.resolve(Keys.getKeyByName("checkpoint failure action"));

                    failureAction.execute((IObject) entry.getState().getValue(messageFieldName));
                }
                return;
            }

            IRecoverStrategy recoverStrategy = IOC.resolve(IOC.resolve(
                    IOC.getKeyForKeyByNameStrategy(),
                    entry.getState().getValue(recoverStrategyFieldName)
            ));

            recoverStrategy.reSend(entry.getState());
        } catch (ResolutionException | ReadValueException | InvalidArgumentException | RecoverStrategyExecutionException
                | ActionExecutionException e) {
            throw new SchedulerActionExecutionException("Error occurred executing checkpoint action.", e);
        }
    }
}
