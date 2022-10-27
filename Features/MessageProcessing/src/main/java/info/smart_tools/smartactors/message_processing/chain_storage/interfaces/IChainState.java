package info.smart_tools.smartactors.message_processing.chain_storage.interfaces;

import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.message_processing_interfaces.ichain_storage.exceptions.ChainModificationException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IReceiverChain;

/**
 *
 */
public interface IChainState {
    /**
     * Modify the chain.
     *
     * @param modification    description of chain modification
     * @return identifier of the modification
     * @throws ChainModificationException if any error occurs
     */
    Object update(IObject modification) throws ChainModificationException;

    /**
     * Cancel a modification.
     *
     * @param modId    identifier of the modification
     * @throws ChainModificationException if any error occurs
     */
    void rollback(Object modId) throws ChainModificationException;

    /**
     * Get current state of the chain
     *
     * @return the chain
     */
    IReceiverChain getCurrent();
}
