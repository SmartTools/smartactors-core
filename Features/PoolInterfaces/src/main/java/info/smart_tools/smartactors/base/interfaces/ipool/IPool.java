package info.smart_tools.smartactors.base.interfaces.ipool;

import info.smart_tools.smartactors.base.interfaces.ipool.exception.GettingFromPoolException;
import info.smart_tools.smartactors.base.interfaces.ipool.exception.PuttingToPoolException;
import info.smart_tools.smartactors.base.interfaces.iresource_source.IResourceSource;

/**
 * Pool interface
 */
public interface IPool extends IResourceSource {
    /**
     * Gets an item from the pool
     * @return found object
     * @throws GettingFromPoolException if value is not found or any error occurred
     */
    Object get() throws GettingFromPoolException;

    /**
     * Puts an item to the pool after using
     * @param item given item
     * @throws PuttingToPoolException any if error occurred
     */
    void put(final Object item) throws PuttingToPoolException;
}
