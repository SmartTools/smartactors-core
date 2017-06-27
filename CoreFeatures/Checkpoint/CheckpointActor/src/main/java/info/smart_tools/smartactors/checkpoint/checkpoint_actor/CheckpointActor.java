package info.smart_tools.smartactors.checkpoint.checkpoint_actor;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecuteException;
import info.smart_tools.smartactors.base.isynchronous_service.exceptions.IllegalServiceStateException;
import info.smart_tools.smartactors.base.isynchronous_service.exceptions.ServiceStartupException;
import info.smart_tools.smartactors.base.isynchronous_service.exceptions.ServiceStopException;
import info.smart_tools.smartactors.checkpoint.checkpoint_actor.wrappers.EnteringMessage;
import info.smart_tools.smartactors.checkpoint.checkpoint_actor.wrappers.FeedbackMessage;
import info.smart_tools.smartactors.checkpoint.checkpoint_actor.wrappers.StartStopMessage;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.SerializeException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import info.smart_tools.smartactors.message_bus.interfaces.imessage_bus_container.exception.SendingMessageException;
import info.smart_tools.smartactors.message_bus.message_bus.MessageBus;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerEntry;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerService;
import info.smart_tools.smartactors.scheduler.interfaces.exceptions.EntryScheduleException;
import info.smart_tools.smartactors.scheduler.interfaces.exceptions.EntryStorageAccessException;

/**
 * Checkpoint actor.
 *
 * <p>
 *     Checkpoint actor uses only thread-safe components so may be created as object without synchronization (i.e. "stateless actor").
 * </p>
 */
public class CheckpointActor {
    private static final String FEEDBACK_CHAIN_NAME = "checkpoint_feedback_chain";
    private static final String CHECKPOINT_ACTION = "checkpoint scheduler action";
    private static final long COMPLETE_ENTRY_RESCHEDULE_DELAY = 1000;

    private final ISchedulerService service;
    private final Object feedbackChainId;

    private final CheckpointSchedulerEntryStorageObserver storageObserver;

    private final IFieldName responsibleCheckpointIdFieldName;
    private final IFieldName checkpointEntryIdFieldName;
    private final IFieldName schedulingFieldName;
    private final IFieldName messageFieldName;
    private final IFieldName prevCheckpointEntryIdFieldName;
    private final IFieldName prevCheckpointIdFieldName;
    private final IFieldName actionFieldName;
    private final IFieldName recoverFieldName;
    private final IFieldName completedFieldName;
    private final IFieldName gotFeedbackFieldName;
    private final IFieldName processorFieldName;

    /**
     * The constructor.
     *
     * @param args    actor description object
     * @throws ResolutionException if error occurs resolving any dependencies
     * @throws ReadValueException if error occurs reading actor description
     * @throws InvalidArgumentException if some methods do not accept our arguments
     * @throws ActionExecuteException if error occurs executing scheduler service activation action
     */
    public CheckpointActor(final IObject args)
            throws ResolutionException, ReadValueException, InvalidArgumentException, ActionExecuteException {
        //
        responsibleCheckpointIdFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "responsibleCheckpointId");
        checkpointEntryIdFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "checkpointEntryId");
        schedulingFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "scheduling");
        messageFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "message");
        prevCheckpointIdFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "prevCheckpointId");
        prevCheckpointEntryIdFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "prevCheckpointEntryId");
        actionFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "action");
        recoverFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "recover");
        completedFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "completed");
        gotFeedbackFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "gotFeedback");
        processorFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "processor");

        String connectionOptionsDependency = (String) args.getValue(
                IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "connectionOptionsDependency"));
        String connectionPoolDependency = (String) args.getValue(
                IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "connectionPoolDependency"));
        String collectionName = (String) args.getValue(
                IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "collectionName"));

        storageObserver = new CheckpointSchedulerEntryStorageObserver();

        Object connectionOptions = IOC.resolve(Keys.getOrAdd(connectionOptionsDependency));
        Object connectionPool = IOC.resolve(Keys.getOrAdd(connectionPoolDependency), connectionOptions);
        service = IOC.resolve(Keys.getOrAdd("new scheduler service"),
                connectionPool,
                collectionName,
                storageObserver);

        IAction<ISchedulerService> activationAction = IOC.resolve(
                Keys.getOrAdd("scheduler service activation action for checkpoint actor"));
        activationAction.execute(service);

        //
        feedbackChainId = IOC.resolve(Keys.getOrAdd("chain_id_from_map_name"), FEEDBACK_CHAIN_NAME);
    }

    /**
     * Handler for messages reached the checkpoint.
     *
     * @param message    the message
     * @throws ReadValueException if error occurs reading something
     * @throws ChangeValueException if error occurs writing something
     * @throws InvalidArgumentException if something goes wrong
     * @throws ResolutionException if error occurs resolving any dependency
     * @throws SendingMessageException if error occurs sending feedback message
     * @throws SerializeException if error occurs serializing message
     */
    public void enter(final EnteringMessage message)
            throws ReadValueException, InvalidArgumentException, ResolutionException, ChangeValueException,
            SendingMessageException, SerializeException {
        IObject originalCheckpointStatus = message.getCheckpointStatus();

        if (null != originalCheckpointStatus) {
            ISchedulerEntry presentEntry = storageObserver
                    .getPresentEntry(originalCheckpointStatus.getValue(checkpointEntryIdFieldName).toString());

            if (null != presentEntry) {
                // If this checkpoint already received the message and has entry for it ...
                // Notify (again) previous checkpoint
                sendFeedback(originalCheckpointStatus, message.getCheckpointId(),
                        presentEntry.getId());

                // And stop processing of the message
                message.getProcessor().getSequence().end();
                return;
            }
        }

        IObject entryArguments = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()));

        entryArguments.setValue(schedulingFieldName, message.getSchedulingConfiguration());
        entryArguments.setValue(messageFieldName, cloneMessage(message.getMessage()));

        if (null != originalCheckpointStatus) {
            entryArguments.setValue(prevCheckpointEntryIdFieldName,
                    originalCheckpointStatus.getValue(responsibleCheckpointIdFieldName));
            entryArguments.setValue(prevCheckpointIdFieldName,
                    originalCheckpointStatus.getValue(checkpointEntryIdFieldName));
        }

        // Checkpoint action will initialize recover strategy
        entryArguments.setValue(recoverFieldName, message.getRecoverConfiguration());
        entryArguments.setValue(actionFieldName, CHECKPOINT_ACTION);
        entryArguments.setValue(processorFieldName, message.getProcessor());

        ISchedulerEntry entry = IOC.resolve(Keys.getOrAdd("new scheduler entry"), entryArguments, service.getEntryStorage());

        // Update checkpoint status in message.
        // Checkpoint status of re-sent messages will be set by checkpoint scheduler action.
        IObject newCheckpointStatus = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()));

        newCheckpointStatus.setValue(responsibleCheckpointIdFieldName, message.getCheckpointId());
        newCheckpointStatus.setValue(checkpointEntryIdFieldName, entry.getId());

        message.setCheckpointStatus(newCheckpointStatus);

        // Send feedback message to previous checkpoint
        if (null != originalCheckpointStatus) {
            sendFeedback(originalCheckpointStatus, message.getCheckpointId(), entry.getId());
        }
    }

    /**
     * Handler for feedback messages.
     *
     * @param message    the message sent by the next checkpoint
     * @throws ReadValueException if error occurs reading values from message
     * @throws EntryScheduleException if error occurs cancelling the entry
     * @throws InvalidArgumentException if something goes wrong
     * @throws ChangeValueException if error occurs updating entry state
     */
    public void feedback(final FeedbackMessage message)
            throws ReadValueException, ChangeValueException, InvalidArgumentException, EntryScheduleException {
        try {
            ISchedulerEntry entry = service.getEntryStorage().getEntry(message.getPrevCheckpointEntryId());

            if (null != entry.getState().getValue(completedFieldName)) {
                // If the entry is already completed then ignore the feedback message
                return;
            }

            entry.scheduleNext(System.currentTimeMillis() + COMPLETE_ENTRY_RESCHEDULE_DELAY);

            entry.getState().setValue(gotFeedbackFieldName, true);
            entry.getState().setValue(completedFieldName, true);
        } catch (EntryStorageAccessException ignore) {
            // There is no entry with required identifier. OK
        }
    }

    /**
     * Start the scheduler.
     *
     * @param message    the message
     * @throws ServiceStartupException if error occurs starting the service
     * @throws IllegalServiceStateException if the service is already running/starting
     */
    public void start(final StartStopMessage message)
            throws ServiceStartupException, IllegalServiceStateException {
        service.start();
    }

    /**
     * Stop the scheduler.
     *
     * @param message    the message
     * @throws ServiceStopException if error occurs stopping the service
     * @throws IllegalServiceStateException if the service is already stopped/not running
     */
    public void stop(final StartStopMessage message)
            throws IllegalServiceStateException, ServiceStopException {
        service.stop();
    }

    private IObject cloneMessage(final IObject message)
            throws SerializeException, ResolutionException {
        String serialized = message.serialize();
        return IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()), serialized);
    }

    private void sendFeedback(final IObject checkpointStatus, final String fromCheckpoint, final String newId)
            throws ResolutionException, SendingMessageException, InvalidArgumentException, ChangeValueException, ReadValueException {
        IObject feedbackMessage = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()));

        feedbackMessage.setValue(responsibleCheckpointIdFieldName, fromCheckpoint);
        feedbackMessage.setValue(checkpointEntryIdFieldName, newId);
        feedbackMessage.setValue(prevCheckpointIdFieldName, checkpointStatus.getValue(responsibleCheckpointIdFieldName));
        feedbackMessage.setValue(prevCheckpointEntryIdFieldName, checkpointStatus.getValue(checkpointEntryIdFieldName));

        MessageBus.send(feedbackMessage, feedbackChainId);
    }
}
