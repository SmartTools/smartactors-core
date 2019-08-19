package info.smart_tools.smartactors.message_processing.object_creation_strategies;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.IReceiverObjectCreator;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.IReceiverObjectListener;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.exeptions.InvalidReceiverPipelineException;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.exeptions.ReceiverObjectCreatorException;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.exeptions.ReceiverObjectListenerException;

import java.util.Collection;
import java.util.Collections;

/**
 * {@link IReceiverObjectCreator Object creator} that sets identifier of object(s) created by previous pipeline step to value of {@code
 * "name"} field of configuration object.
 */
public class SetAddressFromObjectNameReceiverCreator extends BasicIntermediateReceiverObjectCreator {
    private Object objectName;

    private final IFieldName objectNameFieldName;

    /**
     * The constructor.
     *
     * @param underlyingObjectCreator {@link IReceiverObjectCreator} that will create underlying object(s)
     * @param filterConfig            configuration of the step of pipeline
     * @param objectConfig            configuration of the object
     */
    public SetAddressFromObjectNameReceiverCreator(
            final IReceiverObjectCreator underlyingObjectCreator,
            final IObject filterConfig,
            final IObject objectConfig
    ) throws ResolutionException {
        super(underlyingObjectCreator, filterConfig, objectConfig);

        objectNameFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "name");
    }


    @Override
    public void create(final IReceiverObjectListener listener, final IObject config, final IObject context)
            throws ReceiverObjectListenerException, InvalidReceiverPipelineException, ReceiverObjectCreatorException {
        try {
            this.objectName = config.getValue(objectNameFieldName);
        } catch (ReadValueException | InvalidArgumentException e) {
            throw new ReceiverObjectCreatorException(e);
        }

        super.create(listener, config, context);
    }

    @Override
    public Collection<Object> enumIdentifiers(final IObject config, final IObject context)
            throws InvalidReceiverPipelineException, ReceiverObjectCreatorException {
        try {
            Object name = config.getValue(objectNameFieldName);
            return Collections.singletonList(name);
        } catch (ReadValueException | InvalidArgumentException e) {
            throw new ReceiverObjectCreatorException(e);
        }
    }

    @Override
    public void acceptItem(final Object itemId,final Object item)
            throws ReceiverObjectListenerException, InvalidReceiverPipelineException, InvalidArgumentException {
        getListener().acceptItem(objectName, item);
    }
}
