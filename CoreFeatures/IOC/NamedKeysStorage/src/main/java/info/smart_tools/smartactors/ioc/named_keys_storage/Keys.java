package info.smart_tools.smartactors.ioc.named_keys_storage;

import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.ioc.IOC;

/**
 * Service locator for storing named instances of {@link IKey}
 */
@Deprecated
public final class Keys {

    /**
     * Default private constructor
     */
    private Keys() {
    }

    /**
     * Get stored or add new instance of {@link IKey} by given name
     * @param keyName name of instance of {@link IKey}
     * @throws ResolutionException if dependency resolution has been failed
     * @return instance of {@link IKey}
     */
    @Deprecated
    public static IKey getOrAdd(final String keyName)
            throws ResolutionException {
        return (IKey) IOC.resolve(IOC.getKeyForKeyStorage(), keyName);
    }
}
