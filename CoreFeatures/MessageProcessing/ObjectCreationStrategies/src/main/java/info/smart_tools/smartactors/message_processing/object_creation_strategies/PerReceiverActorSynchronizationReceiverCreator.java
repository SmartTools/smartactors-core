package info.smart_tools.smartactors.message_processing.object_creation_strategies;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageReceiver;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.IReceiverObjectCreator;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.exeptions.InvalidReceiverPipelineException;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.exeptions.ReceiverObjectListenerException;

/**
 * {@link IReceiverObjectCreator Object creator} that creates a actor synchronization receivers wrapping each of receivers created on
 * previous step of pipeline.
 */
public class PerReceiverActorSynchronizationReceiverCreator extends BasicIntermediateReceiverObjectCreator {
    /**
     * The constructor.
     *
     * @param underlyingObjectCreator {@link IReceiverObjectCreator} that will create underlying object(s)
     * @param filterConfig            configuration of the step of pipeline
     * @param objectConfig            configuration of the object
     */
    public PerReceiverActorSynchronizationReceiverCreator(
            final IReceiverObjectCreator underlyingObjectCreator,
            final IObject filterConfig,
            final IObject objectConfig
    ) {
        super(underlyingObjectCreator, filterConfig, objectConfig);
    }

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
            throw new InvalidReceiverPipelineException("Underlying item of actor synchronization receiver is not a receiver.", e);
        }

        try {
            IMessageReceiver synchronizedReceiver = IOC.resolve(Keys.getKeyByName("create actor synchronization receiver"), receiverItem);
            getListener().acceptItem(itemId, synchronizedReceiver);
        } catch (ResolutionException e) {
            throw new ReceiverObjectListenerException(e);
        }
    }
}
