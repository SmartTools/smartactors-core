package info.smart_tools.smartactors.base.interfaces.serialization;

import info.smart_tools.smartactors.base.interfaces.serialization.exception.CacheDropException;

public interface ICacheable {

    void dropCache()
            throws CacheDropException;

    void dropCacheFor(final Object key)
            throws CacheDropException;
}
