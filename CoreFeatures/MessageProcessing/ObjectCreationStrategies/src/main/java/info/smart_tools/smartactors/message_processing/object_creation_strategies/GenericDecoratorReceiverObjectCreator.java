package info.smart_tools.smartactors.message_processing.object_creation_strategies;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.IReceiverObjectCreator;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.exeptions.InvalidReceiverPipelineException;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.exeptions.ReceiverObjectListenerException;

/**
 * A receiver creator that wraps objects created on previous step with a decorator.
 *
 * The decorator is resolved using IOC with two arguments - the underlying item and filter configuration. Dependency name is taken from
 * {@code "decoratorDependency"} field of filter configuration.
 */
public class GenericDecoratorReceiverObjectCreator extends BasicIntermediateReceiverObjectCreator {
    private final IFieldName decoratorDependencyFieldName;

    /**
     * The constructor.
     *
     * @param underlyingObjectCreator {@link IReceiverObjectCreator} that will create underlying object(s)
     * @param filterConfig            configuration of the step of pipeline
     * @param objectConfig            configuration of the object
     */
    public GenericDecoratorReceiverObjectCreator(IReceiverObjectCreator underlyingObjectCreator, IObject filterConfig, IObject objectConfig)
            throws ResolutionException {
        super(underlyingObjectCreator, filterConfig, objectConfig);

        decoratorDependencyFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "decoratorDependency");
    }

    @Override
    public void acceptItem(Object itemId, Object item)
            throws ReceiverObjectListenerException, InvalidReceiverPipelineException, InvalidArgumentException {
        Object decorator;

        try {
            decorator = IOC.resolve(
                    IOC.resolve(IOC.getKeyForKeyStorage(), getFilterConfig().getValue(decoratorDependencyFieldName)),
                    item,
                    getFilterConfig()
            );
        } catch (ResolutionException | ReadValueException | InvalidArgumentException e) {
            throw new ReceiverObjectListenerException("Error occurred creating receiver decorator.", e);
        }

        getListener().acceptItem(itemId, decorator);
    }
}
