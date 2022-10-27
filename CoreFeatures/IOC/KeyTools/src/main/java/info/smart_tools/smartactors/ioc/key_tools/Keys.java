package info.smart_tools.smartactors.ioc.key_tools;

import info.smart_tools.smartactors.ioc.exception.DeletionException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.ioc.IOC;

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
     * Resolve instance of {@link IKey} by given name
     * @param keyName name of instance of {@link IKey}
     * @throws ResolutionException if dependency resolution has failed
     * @return instance of {@link IKey}
     */
    @Deprecated
    public static IKey resolveByName(final String keyName)
            throws ResolutionException {
        return (IKey) IOC.resolve(IOC.getKeyForKeyByNameStrategy(), keyName);
    }

    /**
     * Resolve instance of {@link IKey} by given name
     * @param keyName name of instance of {@link IKey}
     * @throws ResolutionException if dependency resolution has failed
     * @return instance of {@link IKey}
     */
    public static IKey getKeyByName(final String keyName)
            throws ResolutionException {
        return (IKey) IOC.resolve(IOC.getKeyForKeyByNameStrategy(), keyName);
    }

    /**
     * Unregister dependencies of {@link IKey}s by given names to use in bootstrap reverting procedures
     * @param keyNames array of names of instances of {@link IKey}
     */
    public static void unregisterByNames(final String[] keyNames) {
        for(String keyName : keyNames) {
            try {
                IOC.unregister(Keys.getKeyByName(keyName));
            } catch (DeletionException e) {
                System.out.println("[WARNING] Deregistration of key '"+keyName+"' failed.");
            } catch (ResolutionException e) { }
        }
    }
}
