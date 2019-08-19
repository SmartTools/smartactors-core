package info.smart_tools.smartactors.message_processing.object_creation_strategies;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.irouter.IRouter;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageReceiver;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.IReceiverObjectListener;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.exeptions.InvalidReceiverPipelineException;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.exeptions.ReceiverObjectListenerException;

/**
 * {@link IReceiverObjectListener Object listener} that registers passed receivers in a router.
 */
public class RouterRegistrationObjectListener implements IReceiverObjectListener {
    @Override
    public void acceptItem(final Object itemId, final Object item)
            throws ReceiverObjectListenerException, InvalidReceiverPipelineException, InvalidArgumentException {
        if (null == item) {
            throw new InvalidArgumentException("Item is null.");
        }

        if (null == itemId) {
            throw new InvalidReceiverPipelineException(
                    "Item identifier is null. The filter defining object address is missing or does not work correct.");
        }

        IMessageReceiver receiver;

        try {
            receiver = (IMessageReceiver) item;
        } catch (ClassCastException e) {
            throw new InvalidReceiverPipelineException(
                    "Item is not a message receiver.");
        }

        try {
            IRouter router = IOC.resolve(Keys.getKeyByName(IRouter.class.getCanonicalName()));

            router.register(itemId, receiver);
        } catch (ResolutionException e) {
            throw new ReceiverObjectListenerException(e);
        }
    }

    @Override
    public void endItems() throws ReceiverObjectListenerException, InvalidReceiverPipelineException {

    }
}
