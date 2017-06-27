package info.smart_tools.smartactors.message_processing.object_creation_strategies;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageReceiver;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.IReceiverObjectCreator;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.exeptions.InvalidReceiverPipelineException;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.exeptions.ReceiverObjectCreatorException;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.exeptions.ReceiverObjectListenerException;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;

/**
 * {@link IReceiverObjectCreator Object creator} that for each method of each passed object creates a invoker receiver that invokes that
 * method.
 */
public class UserObjectMethodInvokerReceiverCreator extends BasicIntermediateReceiverObjectCreator {
    /**
     * The constructor.
     *
     * @param underlyingObjectCreator   {@link IReceiverObjectCreator} that will create underlying object(s)
     * @param filterConfig              configuration of the step of pipeline
     * @param objectConfig              configuration of the object
     */
    public UserObjectMethodInvokerReceiverCreator(
            final IReceiverObjectCreator underlyingObjectCreator,
            final IObject filterConfig,
            final IObject objectConfig) {
        super(underlyingObjectCreator, filterConfig, objectConfig);
    }

    @Override
    public Collection<Object> enumIdentifiers(IObject config, IObject context)
            throws InvalidReceiverPipelineException, ReceiverObjectCreatorException {
        // TODO:: Implement some way to predict list of object method names
        throw new InvalidReceiverPipelineException("Enumeration of user object methods is not implemented.");
    }

    @Override
    public void acceptItem(Object itemId, Object item)
            throws ReceiverObjectListenerException, InvalidReceiverPipelineException, InvalidArgumentException {
        if (null == item) {
            throw new InvalidArgumentException("Item is null.");
        }

        try {
            for (Method method : item.getClass().getDeclaredMethods()) {
                if (method.isSynthetic() || !Modifier.isPublic(method.getModifiers())) {
                    continue;
                }

                IMessageReceiver methodInvoker = IOC.resolve(
                        Keys.getOrAdd("method invoker receiver"),
                        item,
                        method,
                        getFilterConfig()
                );

                getListener().acceptItem(method.getName(), methodInvoker);
            }
        } catch (ResolutionException e) {
            throw new ReceiverObjectListenerException(e);
        }
    }
}
