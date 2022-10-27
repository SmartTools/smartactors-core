package info.smart_tools.smartactors.message_processing_interfaces.ichain_storage;

import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.message_processing_interfaces.ichain_storage.exceptions.ChainCreationException;
import info.smart_tools.smartactors.message_processing_interfaces.ichain_storage.exceptions.ChainModificationException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IReceiverChain;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.ChainNotFoundException;

import java.util.List;

/**
 * Storage of {@link IReceiverChain}'s.
 */
public interface IChainStorage {
    /**
     * Create a chain from a description object and store it with given identifier.
     *
     * @param chainId        identifier to store chain with
     * @param description    object describing the chain
     * @throws ChainCreationException if any error occurs
     */
    void register(Object chainId, IObject description) throws ChainCreationException;

    /**
     * Remove a chain with given identifier.
     *
     * @param chainId        identifier to store chain with
     */
    void unregister(Object chainId);

    /**
     * Modify a chain and store the modified chain with the same identifier.
     *
     * @param chainId identifier of the chain to update
     * @param modification description of the chain modification to perform
     * @return identifier of the chain mutation
     * @throws ChainNotFoundException if there is no chain with given identifier registered
     * @throws ChainModificationException if error occurs modifying the chain
     */
    Object update(Object chainId, IObject modification) throws ChainNotFoundException, ChainModificationException;

    /**
     * Cancel modification performed by {@link #update(Object, IObject)} keeping all the rest applied (reapplying the ones applied after the
     * cancelled one).
     *
     * @param chainId    identifier of the chain
     * @param modId      identifier of the modification (as returned by {@link #update(Object, IObject)})
     * @throws ChainNotFoundException if there is no chain with given identifier
     * @throws ChainModificationException if error occurs re-applying modifications applied after the cancelled one
     */
    void rollback(Object chainId, Object modId) throws ChainNotFoundException, ChainModificationException;

    /**
     * Find a chain associated with given identifier.
     *
     * @param chainId    identifier o a chain
     * @return the found chain
     * @throws ChainNotFoundException if there is no chain associated with given identifier
     */
    IReceiverChain resolve(Object chainId) throws ChainNotFoundException;

    /**
     * Get list of identifiers of all chains stored in this storage.
     *
     * @return list of identifiers of all chains
     */
    List<Object> enumerate();
}
