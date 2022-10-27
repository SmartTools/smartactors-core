package info.smart_tools.smartactors.message_processing.object_creation_strategies;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageReceiver;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.IReceiverObjectCreator;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.exeptions.InvalidReceiverPipelineException;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.exeptions.ReceiverObjectCreatorException;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.exeptions.ReceiverObjectListenerException;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * {@link IReceiverObjectCreator Object creator} that joins few receivers having unique identifiers into one {@link
 * HandlerRouterReceiverCreator}.
 */
public class HandlerRouterReceiverCreator extends BasicIntermediateReceiverObjectCreator {
    private final Map<Object, IMessageReceiver> handlersMap;

    /**
     * The constructor.
     *
     * @param underlyingObjectCreator {@link IReceiverObjectCreator} that will create underlying object(s)
     * @param filterConfig            configuration of the step of pipeline
     * @param objectConfig            configuration of the object
     */
    public HandlerRouterReceiverCreator(IReceiverObjectCreator underlyingObjectCreator, IObject filterConfig, IObject objectConfig) {
        super(underlyingObjectCreator, filterConfig, objectConfig);

        handlersMap = new HashMap<>();
    }

    @Override
    public Collection<Object> enumIdentifiers(IObject config, IObject context)
            throws InvalidReceiverPipelineException, ReceiverObjectCreatorException {
        return Collections.singletonList(null);
    }

    @Override
    public void acceptItem(Object itemId, Object item)
            throws ReceiverObjectListenerException, InvalidReceiverPipelineException, InvalidArgumentException {
        if (null == item) {
            throw new InvalidArgumentException("Item is null.");
        }

        IMessageReceiver receiverItem;

        try {
            receiverItem = (IMessageReceiver) item;
        } catch (ClassCastException e) {
            throw new InvalidReceiverPipelineException("Underlying object is not a receiver.", e);
        }

        if (null == itemId) {
            throw new InvalidReceiverPipelineException("Underlying object has no identifier.");
        }

        if (null != handlersMap.put(itemId, receiverItem)) {
            throw new InvalidReceiverPipelineException("Duplicate underlying object identifier: " + String.valueOf(itemId));
        }
    }

    @Override
    public void endItems()
            throws ReceiverObjectListenerException, InvalidReceiverPipelineException {
        try {
            IMessageReceiver resultingReceiver = IOC.resolve(Keys.getKeyByName("create handler router receiver"), handlersMap);

            getListener().acceptItem(null, resultingReceiver);
            getListener().endItems();
        } catch (InvalidArgumentException | ResolutionException e) {
            throw new ReceiverObjectListenerException(e);
        }
    }
}
