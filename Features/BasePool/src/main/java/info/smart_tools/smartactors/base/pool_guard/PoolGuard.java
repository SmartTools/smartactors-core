package info.smart_tools.smartactors.base.pool_guard;

import info.smart_tools.smartactors.base.interfaces.ipool.IPool;
import info.smart_tools.smartactors.base.interfaces.ipool_guard.IPoolGuard;
import info.smart_tools.smartactors.base.interfaces.ipool_guard.exception.PoolGuardException;

/**
 * Implementation of {@link IPoolGuard}
 */
public class PoolGuard implements IPoolGuard {
    private IPool pool;
    private Object currentObject;

    /**
     * Constructor for PoolGuard
     * @param pool the using pool
     * @throws PoolGuardException if any errors occurred
     */
    public PoolGuard(final IPool pool) throws PoolGuardException {
        try {
            this.pool = pool;
            this.currentObject = pool.get();
        } catch (Exception e) {
            throw new PoolGuardException("PoolGuard cannot be initialized", e);
        }
    }


    /**
     * @return free item from pool
     */
    public Object getObject() {
        return currentObject;
    }

    /**
     * Return object to pool if object is no needed more
     */
    public void close() {
        try {
            pool.put(currentObject);
        } catch (Exception e) {
            throw new RuntimeException("PoolGuard could not restore current item.", e);
        }
    }
}
