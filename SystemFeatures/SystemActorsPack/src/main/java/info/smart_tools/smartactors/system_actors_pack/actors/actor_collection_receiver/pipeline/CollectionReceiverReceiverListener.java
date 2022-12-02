package info.smart_tools.smartactors.system_actors_pack.actors.actor_collection_receiver.pipeline;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageReceiver;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.IReceiverObjectListener;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.exeptions.InvalidReceiverPipelineException;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.exeptions.ReceiverObjectListenerException;

/**
 * Implementation of {@link IReceiverObjectListener} that accepts a single receiver and sores it.
 */
public class CollectionReceiverReceiverListener implements IReceiverObjectListener {

    private IMessageReceiver receiver;
    private boolean finished;

    @Override
    public void acceptItem(final Object itemId, final Object item)
        throws ReceiverObjectListenerException, InvalidReceiverPipelineException, InvalidArgumentException {
        if (null == item) {
            throw new InvalidArgumentException("Item is null.");
        }

        IMessageReceiver receiverItem;

        try {
            receiverItem = (IMessageReceiver) item;
        } catch (ClassCastException e) {
            throw new InvalidReceiverPipelineException("Item is not a message receiver.", e);
        }

        if (null != receiver) {
            throw new InvalidReceiverPipelineException("Collection receiver supports only objects with single external receiver.");
        }

        receiver = receiverItem;
    }

    @Override
    public void endItems()
        throws ReceiverObjectListenerException, InvalidReceiverPipelineException {
        if (null == receiver) {
            throw new InvalidReceiverPipelineException("No items passed to collection receiver.");
        }

        finished = true;
    }

    /**
     * Returns the accepted receiver.
     *
     * @return the accepted receiver
     * @throws InvalidReceiverPipelineException if pipeline did not create receiver synchronously
     */
    public IMessageReceiver getReceiver() throws InvalidReceiverPipelineException {
        if (!finished) {
            throw new InvalidReceiverPipelineException("Object creation not finished synchronously.");
        }

        return receiver;
    }
}
