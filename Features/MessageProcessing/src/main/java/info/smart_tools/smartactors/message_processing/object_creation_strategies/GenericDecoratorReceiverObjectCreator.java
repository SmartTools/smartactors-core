package info.smart_tools.smartactors.message_processing.object_creation_strategies;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.IReceiverObjectCreator;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.exeptions.InvalidReceiverPipelineException;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.exeptions.ReceiverObjectListenerException;

/**
 * A receiver creator that wraps objects created on previous step with a decorator.
 *
 * <p>
 * The decorator is resolved using IOC with 4 arguments: the underlying item, filter configuration, object configuration and pipeline
 * creation context.
 * </p>
 *
 * <p>
 * Dependency name is taken from {@code "decoratorDependency"} field of filter configuration.
 * </p>
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
    public GenericDecoratorReceiverObjectCreator(
            final IReceiverObjectCreator underlyingObjectCreator, final IObject filterConfig, final IObject objectConfig)
            throws ResolutionException {
        super(underlyingObjectCreator, filterConfig, objectConfig);

        decoratorDependencyFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "decoratorDependency");
    }

    @Override
    public void acceptItem(final Object itemId, final Object item)
            throws ReceiverObjectListenerException, InvalidReceiverPipelineException, InvalidArgumentException {
        Object decorator;

        try {
            decorator = IOC.resolve(
                    IOC.resolve(IOC.getKeyForKeyByNameStrategy(), getFilterConfig().getValue(decoratorDependencyFieldName)),
                    item,
                    getFilterConfig(),
                    getObjectConfig(),
                    getContext()
            );
        } catch (ResolutionException | ReadValueException | InvalidArgumentException e) {
            throw new ReceiverObjectListenerException("Error occurred creating receiver decorator.", e);
        }

        getListener().acceptItem(itemId, decorator);
    }
}
