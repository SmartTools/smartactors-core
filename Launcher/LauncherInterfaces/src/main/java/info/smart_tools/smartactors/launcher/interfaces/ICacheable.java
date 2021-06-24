package info.smart_tools.smartactors.launcher.interfaces;

import info.smart_tools.smartactors.launcher.interfaces.exception.CacheDropException;

public interface ICacheable {

    void dropCache()
            throws CacheDropException;

    void dropCacheFor(final Object key)
            throws CacheDropException;
}
