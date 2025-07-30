package info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.exeptions.InvalidReceiverPipelineException;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.exeptions.ReceiverObjectListenerException;

/**
 * Interface for the object handling objects created by a {@link IReceiverObjectCreator creator}.
 *
 * Implementations may register receivers in router or pass to another listener modifying, wrapping them or changing identifiers.
 */
public interface IReceiverObjectListener {
    /**
     * Accept a created object.
     *
     * Item identifier may be an identifier of a method of a user object, the name the receiver should be registered with in global router
     * or {@code null} if the receiver creator does not provide any identifier.
     *
     * @param itemId    identifier of the item
     * @param item      the item itself
     * @throws ReceiverObjectListenerException if any error occurs
     * @throws InvalidReceiverPipelineException if receiver pipeline is not valid
     * @throws InvalidArgumentException if {@code item} is {@code null}
     * @see InvalidReceiverPipelineException
     */
    void acceptItem(Object itemId, Object item)
            throws ReceiverObjectListenerException, InvalidReceiverPipelineException, InvalidArgumentException;

    /**
     * End accepting items.
     *
     * @throws ReceiverObjectListenerException if any error occurs
     * @throws InvalidReceiverPipelineException if receiver pipeline is not valid
     * @see InvalidReceiverPipelineException
     */
    void endItems() throws ReceiverObjectListenerException, InvalidReceiverPipelineException;
}
