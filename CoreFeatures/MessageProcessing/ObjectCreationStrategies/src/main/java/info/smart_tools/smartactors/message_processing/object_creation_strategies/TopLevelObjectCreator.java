package info.smart_tools.smartactors.message_processing.object_creation_strategies;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
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
 * {@link IReceiverObjectCreator Object creator} that resolves the object using key name defined in configuration.
 *
 * This is probably the only one creator that may be a singleton.
 */
public class TopLevelObjectCreator implements IReceiverObjectCreator {
    private final IFieldName dependencyFieldName;
    private final IFieldName topLevelObjectFieldName;

    public TopLevelObjectCreator()
            throws ResolutionException {
        dependencyFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "dependency");
        topLevelObjectFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "topLevelObject");
    }

    @Override
    public void create(final IReceiverObjectListener listener, final IObject config, final IObject context)
            throws ReceiverObjectListenerException, InvalidReceiverPipelineException, ReceiverObjectCreatorException {
        try {
            Object object = IOC.resolve(
                    IOC.resolve(IOC.getKeyForKeyByNameStrategy(), config.getValue(dependencyFieldName)),
                    config
            );

            context.setValue(topLevelObjectFieldName, object);

            listener.acceptItem(null, object);
            listener.endItems();
        } catch (InvalidArgumentException | ReadValueException | ChangeValueException | ResolutionException e) {
            throw new ReceiverObjectCreatorException(e);
        }
    }

    @Override
    public Collection<Object> enumIdentifiers(final IObject config, final IObject context)
            throws InvalidReceiverPipelineException, ReceiverObjectCreatorException {
        return Collections.singletonList(null);
    }
}
