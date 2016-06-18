package info.smart_tools.smartactors.core.named_keys_storage;

import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.ioc.IOC;

/**
 * Service locator for storing named instances of {@link IKey}
 */
public final class Keys {

    /**
     * Default private constructor
     */
    private Keys() {
    }

    /**
     * Get stored or add new instance of {@link IKey} by given name
     * @param <T> type of stored object by instance of {@link IKey}
     * @param keyName name of instance of {@link IKey}
     * @throws ResolutionException if dependency resolution has been failed
     * @return instance of {@link IKey}
     */
    public static <T> IKey<T> getOrAdd(final String keyName)
            throws ResolutionException {
        return (IKey<T>) IOC.resolve(IOC.getKeyForKeyStorage(), keyName);
    }
}
