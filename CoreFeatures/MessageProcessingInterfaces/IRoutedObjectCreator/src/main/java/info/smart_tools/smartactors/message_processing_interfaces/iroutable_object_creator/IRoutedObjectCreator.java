package info.smart_tools.smartactors.message_processing_interfaces.iroutable_object_creator;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.message_processing_interfaces.iroutable_object_creator.exceptions.ObjectCreationException;
import info.smart_tools.smartactors.message_processing_interfaces.irouter.IRouter;

/**
 * Interface for a strategy of creation of some kind of objects able (through one or more receivers registered in router) to receive
 * messages.
 */
public interface IRoutedObjectCreator {
    /**
     * Create a object and register receiver(s) associated with it in the router.
     *
     * @param router         the router to register receivers associated with the created object
     * @param description    configuration object describing the object to create
     * @throws ObjectCreationException if any error occurs
     * @throws InvalidArgumentException if {@code router} is {@code null}
     * @throws InvalidArgumentException if {@code description} is {@code null}
     */
    void createObject(IRouter router, IObject description)
            throws ObjectCreationException, InvalidArgumentException;
}
