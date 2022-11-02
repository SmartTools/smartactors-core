package info.smart_tools.smartactors.message_processing_plugins.standard_object_creators_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.iroutable_object_creator.IRoutedObjectCreator;
import info.smart_tools.smartactors.message_processing_interfaces.iroutable_object_creator.exceptions.ObjectCreationException;
import info.smart_tools.smartactors.message_processing_interfaces.irouter.IRouter;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageReceiver;

/**
 * Implementation of {@link IRoutedObjectCreator} that creates receiver of some class define in configuration.
 *
 * Expects the following description fields:
 *
 * <pre>
 *     {
 *         "dependency": "org.my.receiver.Receiver",// name of receiver dependency (should implement {@link IMessageReceiver})
 *         "name": "receiver"                       // identifier of the receiver in the router
 *     }
 * </pre>
 */
public class RawObjectCreator implements IRoutedObjectCreator {
    private final IFieldName dependencyFieldName;
    private final IFieldName nameFieldName;

    /**
     * The constructor.
     *
     * @throws ResolutionException if fails to resolve any dependency
     */
    public RawObjectCreator()
            throws ResolutionException {
        dependencyFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "dependency");
        nameFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "name");
    }

    @Override
    public void createObject(final IRouter router, final IObject description)
            throws ObjectCreationException, InvalidArgumentException {
        if (null == router) {
            throw new InvalidArgumentException("Router should not be null.");
        }

        if (null == description) {
            throw new InvalidArgumentException("Description should not be null.");
        }

        try {
            Object address = IOC.resolve(Keys.getKeyByName("route_from_object_name"), description.getValue(nameFieldName));
            Object receiver = IOC.resolve(Keys.getKeyByName(String.valueOf(description.getValue(dependencyFieldName))), description);

            if (!(receiver instanceof IMessageReceiver)) {
                throw new ObjectCreationException("Resolved dependency does not implement receiver interface.");
            }

            router.register(address, (IMessageReceiver) receiver);
        } catch (ReadValueException | InvalidArgumentException | ResolutionException e) {
            throw new ObjectCreationException("Error occurred creating the receiver.", e);
        }
    }
}
