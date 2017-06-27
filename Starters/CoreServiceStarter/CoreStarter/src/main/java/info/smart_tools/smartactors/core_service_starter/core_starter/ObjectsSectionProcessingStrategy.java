package info.smart_tools.smartactors.core_service_starter.core_starter;

import info.smart_tools.smartactors.configuration_manager.interfaces.iconfiguration_manager.ISectionStrategy;
import info.smart_tools.smartactors.configuration_manager.interfaces.iconfiguration_manager.exceptions.ConfigurationProcessingException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.exception.SerializeException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.message_processing_interfaces.iroutable_object_creator.IRoutedObjectCreator;
import info.smart_tools.smartactors.message_processing_interfaces.iroutable_object_creator.exceptions.ObjectCreationException;
import info.smart_tools.smartactors.message_processing_interfaces.irouter.IRouter;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;

import java.util.List;

/**
 * Creates objects using configuration.
 * <p>
 * Expects the following configuration format:
 * <p>
 * <pre>
 *     {
 *         "objects": [
 *             {
 *                 "kind": "actor",
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

    private final IFieldName objectKindField;

    /**
     * The constructor.
     *
     * @throws ResolutionException if fails to resolve any dependencies
     */
    public ObjectsSectionProcessingStrategy()
            throws ResolutionException {
        this.name = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "objects");
        this.objectKindField = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "kind");
    }

    @Override
    public void onLoadConfig(final IObject config) throws ConfigurationProcessingException {
        try {
            List<IObject> section = (List<IObject>) config.getValue(name);
            IRouter router = IOC.resolve(Keys.getOrAdd(IRouter.class.getCanonicalName()));

            for (IObject objDesc : section) {
                Object kindId = objDesc.getValue(objectKindField);
                IRoutedObjectCreator objectCreator = IOC.resolve(
                        Keys.getOrAdd(IRoutedObjectCreator.class.getCanonicalName() + "#" + String.valueOf(kindId)));

                try {
                    objectCreator.createObject(router, objDesc);
                } catch (ObjectCreationException e) {
                    throw new ConfigurationProcessingException(
                            String.format(
                                    "Could not create object %s described in \"objects\" section: %s",
                                    objDesc.serialize(), e.getMessage()),
                            e);
                }
            }
        } catch (ResolutionException | ReadValueException | InvalidArgumentException | SerializeException e) {
            throw new ConfigurationProcessingException("Error occurred loading \"objects\" configuration section.", e);
        }
    }

    @Override
    public IFieldName getSectionName() {
        return name;
    }
}
