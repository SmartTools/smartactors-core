package info.smart_tools.smartactors.ioc.ioc;

import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.ioc.iioccontainer.IContainer;
import info.smart_tools.smartactors.ioc.exception.DeletionException;
import info.smart_tools.smartactors.ioc.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.ioc_container.Container;

/**
 * Realization of IOC Container by ServiceLocator pattern
 */
public final class IOC {

    /**
     * Implementation of {@link IContainer}.
     * Will be initialized by default implementation of {@link IContainer}
     * ReInitialization possible only with using java reflection API
     * Example:
     * <pre>
     * {@code
     * Field field = IOC.class.getDeclaredField("container");
     * field.setAccessible(true);
     * field.set(null, new Object());
     * field.setAccessible(false);
     * }
     * </pre>
     */
    private static IContainer container;

    /**
     * Initialize IOC by default implementation of {@link IContainer}
     */

    static {
        container = new Container();
    }

    /**
     * Default private constructor
     */
    private IOC() {
    }

    /**
     * Return specific instance of {@link IKey} for container ID
     * @return instance of {@link IKey}
     */
    public static IKey getIocKey() {
        return container.getIocKey();
    }

    /**
     * Return specific instance of {@link IKey} for resolve dependencies from key storage
     * @return instance of {@link IKey}
     */
    @Deprecated
    public static IKey getKeyForKeyStorage() { return container.getKeyForKeyByNameStrategy(); }

    /**
     * Return specific instance of {@link IKey} for strategy of resolving key by name
     * @return instance of {@link IKey}
     */
    @Deprecated
    public static IKey getKeyForKeyByNameResolutionStrategy() {
        return container.getKeyForKeyByNameStrategy();
    }

    /**
     * Return specific instance of {@link IKey} for strategy of resolving key by name
     * @return instance of {@link IKey}
     */
    public static IKey getKeyForKeyByNameStrategy() {
        return container.getKeyForKeyByNameStrategy();
    }

    /**
     * Resolve dependency by given given {@link IKey} instance and args
     * @param key instance of {@link IKey}
     * @param args needed parameters for resolve dependency
     * @param <T> type of class for resolution
     * @return instance of class with classId identifier
     * @throws ResolutionException if resolution is impossible because of any errors
     */
    public static <T> T resolve(final IKey key, final Object ... args)
            throws ResolutionException {
        return (T) container.resolve(key, args);
    }

    /**
     * Register new dependency by instance of {@link IKey}
     * @param key instance of {@link IKey}
     * @param strategy instance of {@link IStrategy}
     * @throws RegistrationException when registration is impossible because of any error
     */
    public static void register(final IKey key, final IStrategy strategy)
            throws RegistrationException {
        container.register(key, strategy);
    }

    /**
     * Remove dependency with given key
     * @param key instance of {@link IKey}
     * @throws DeletionException if any errors occurred
     */
    @Deprecated
    public static void remove(final IKey key)
            throws DeletionException {
        container.unregister(key);
    }

    /**
     * Unregister dependency with given key
     * @param key instance of {@link IKey}
     * @throws DeletionException if any errors occurred
     * @return the previous instance of {@link IStrategy} associated with <tt>key</tt>,
     *         or <tt>null</tt> if there was no association for <tt>key</tt>.
     */
    public static IStrategy unregister(final IKey key)
            throws DeletionException {
        return container.unregister(key);
    }
}
