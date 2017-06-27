package info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces;

import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.exeptions.InvalidReceiverPipelineException;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.exeptions.ReceiverObjectCreatorException;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.exeptions.ReceiverObjectListenerException;

import java.util.Collection;

/**
 * Interface for an object creating a receiver object(s).
 */
public interface IReceiverObjectCreator {
    /**
     * Create the objects and pass them to listener.
     *
     * @param listener    the listener to pass created objects to
     * @param config      logical object configuration
     * @param context     object creation context
     * @throws ReceiverObjectListenerException if error occurs notifying listener on created object
     * @throws InvalidReceiverPipelineException if receiver pipeline is not valid
     * @throws ReceiverObjectCreatorException if any other error occurs
     */
    void create(IReceiverObjectListener listener, IObject config, IObject context)
            throws ReceiverObjectListenerException, InvalidReceiverPipelineException, ReceiverObjectCreatorException;

    /**
     * Get collection of identifiers of objects that will be created by {@link #create(IReceiverObjectListener, IObject, IObject)} method
     * (but do not create any actual objects if possible).
     *
     * @param config     logical object configuration
     * @param context    object creation context
     * @return collection of identifiers of objects that will be created
     * @throws InvalidReceiverPipelineException if receiver pipeline is not valid
     * @throws ReceiverObjectCreatorException if any other error occurs
     */
    Collection<Object> enumIdentifiers(IObject config, IObject context)
            throws InvalidReceiverPipelineException, ReceiverObjectCreatorException;
}
