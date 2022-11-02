package info.smart_tools.smartactors.base.interfaces.ipool_guard;

import info.smart_tools.smartactors.base.interfaces.ipool.IPool;

/**
 * PoolGuard interface
 * provides methods for switch current {@link IPool}
 * by other
 * @since 1.7+
 */
public interface IPoolGuard extends AutoCloseable {

    /**
     * Get the free item from pool
     * @return object
     */
    Object getObject();

    /**
     * Return object to pool if object is no needed more
     */
    void close();
}
