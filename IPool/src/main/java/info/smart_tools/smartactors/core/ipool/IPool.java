package info.smart_tools.smartactors.core.ipool;

import info.smart_tools.smartactors.core.ipool.exception.PoolPutException;
import info.smart_tools.smartactors.core.ipool.exception.PoolTakeException;

/**
 * Pool interface
 */
public interface IPool {
    /**
     * Get an item from the pool
     * @return found object
     * @throws PoolTakeException if value is not found or any error occurred
     */
    Object take() throws PoolTakeException;

    /**
     * Stores an value to the pool after using
     * @param value given value
     * @throws PoolTakeException any if error occurred
     */
    void put(final Object value) throws PoolPutException;
}
