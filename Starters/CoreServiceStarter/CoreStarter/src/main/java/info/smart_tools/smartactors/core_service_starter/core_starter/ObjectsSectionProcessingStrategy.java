package info.smart_tools.smartactors.core_service_starter.core_starter;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.configuration_manager.interfaces.iconfiguration_manager.ISectionStrategy;
import info.smart_tools.smartactors.configuration_manager.interfaces.iconfiguration_manager.exceptions.ConfigurationProcessingException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.IReceiverObjectCreator;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.IReceiverObjectListener;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.exeptions.InvalidReceiverPipelineException;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.exeptions.ReceiverObjectCreatorException;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.exeptions.ReceiverObjectListenerException;

import java.util.List;

/**
 * Creates objects using configuration.
 *
 * Expects the following configuration format:
 *
 * <pre>
 *     {
 *         "objects": [
 *             {
 *                 "filters": [
 *                      {
 *                          "dependency": "  . . .  ",
 *                          . . .
 *                      },
 *                      . . .
 *                 ]
 *                 // . . .
 *             },
 *             {
 *                 // . . .
 *             }
 *         ]
 *     }
 * </pre>
 */
public class ObjectsSectionProcessingStrategy implements ISectionStrategy {
    private final IFieldName name;

    /**
     * The constructor.
     *
     * @throws ResolutionException if fails to resolve any dependencies
     */
    public ObjectsSectionProcessingStrategy()
            throws ResolutionException {
        this.name = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "objects");
    }

    @Override
    public void onLoadConfig(final IObject config) throws ConfigurationProcessingException {
        try {
            List<IObject> section = (List<IObject>) config.getValue(name);

            for (IObject objDesc : section) {
                IReceiverObjectListener listener = IOC.resolve(Keys.getOrAdd("global router registration receiver object listener"));
                IReceiverObjectCreator creator = IOC.resolve(Keys.getOrAdd("full receiver object creator"), objDesc);
                IObject context = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()));
                creator.create(listener, objDesc, context);
            }
        } catch (InvalidReceiverPipelineException | ResolutionException | InvalidArgumentException | ReadValueException
                | ReceiverObjectCreatorException | ReceiverObjectListenerException e) {
            throw new ConfigurationProcessingException("Error occurred loading \"objects\" configuration section.", e);
        }
    }

    @Override
    public IFieldName getSectionName() {
        return name;
    }
}
