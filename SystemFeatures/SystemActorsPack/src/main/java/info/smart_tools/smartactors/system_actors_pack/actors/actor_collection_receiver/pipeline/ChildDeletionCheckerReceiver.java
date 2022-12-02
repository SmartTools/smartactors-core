package info.smart_tools.smartactors.system_actors_pack.actors.actor_collection_receiver.pipeline;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageReceiver;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.AsynchronousOperationException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.MessageReceiveException;
import info.smart_tools.smartactors.system_actors_pack.actors.actor_collection_receiver.interfaces.IChildDeletionCheckStrategy;
import info.smart_tools.smartactors.system_actors_pack.actors.actor_collection_receiver.interfaces.exception.DeletionCheckException;

/**
 * Receiver that checks if a child receiver it wraps should be deleted and deletes it if necessary.
 */
public class ChildDeletionCheckerReceiver implements IMessageReceiver {

    private final IMessageReceiver underlyingReceiver;
    private final IObject creationContext;
    private final IChildDeletionCheckStrategy deletionCheckStrategy;
    private final IAction<IObject> deletionAction;

    /**
     * The constructor.
     *
     * @param underlyingReceiver       the underlying receiver
     * @param creationContext          receiver pipeline creation context
     * @param deletionCheckStrategy    check strategy
     * @param deletionAction           action deleting the receiver
     */
    public ChildDeletionCheckerReceiver(
        final IMessageReceiver underlyingReceiver,
        final IObject creationContext,
        final IChildDeletionCheckStrategy deletionCheckStrategy,
        final IAction<IObject> deletionAction
    ) {
        this.underlyingReceiver = underlyingReceiver;
        this.creationContext = creationContext;
        this.deletionCheckStrategy = deletionCheckStrategy;
        this.deletionAction = deletionAction;
    }

    private void checkDeletion(final IMessageProcessor messageProcessor) throws
        ActionExecutionException, DeletionCheckException, InvalidArgumentException {
        messageProcessor.resetEnvironment();
        if (deletionCheckStrategy.checkDelete(creationContext, messageProcessor.getEnvironment())) {
            deletionAction.execute(creationContext);
        }
    }

    @Override
    public void receive(final IMessageProcessor processor)
        throws MessageReceiveException, AsynchronousOperationException {
        try {
            underlyingReceiver.receive(processor);

            checkDeletion(processor);
        } catch (MessageReceiveException e) {
            try {
                checkDeletion(processor);
            } catch (ActionExecutionException | DeletionCheckException | InvalidArgumentException ee) {
                e.addSuppressed(ee);
            }

            throw new MessageReceiveException(e);
        } catch (ActionExecutionException | DeletionCheckException | InvalidArgumentException e) {
            throw new MessageReceiveException(e);
        }
    }

    @Override
    public void dispose() {
        // TODO: implement
    }
}
