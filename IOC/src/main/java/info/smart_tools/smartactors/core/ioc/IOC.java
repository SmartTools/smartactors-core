package info.smart_tools.smartactors.core.ioc;

import info.smart_tools.smartactors.core.iioccontainer.IContainer;
import info.smart_tools.smartactors.core.iioccontainer.exception.DeletionException;
import info.smart_tools.smartactors.core.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.ioc_container.Container;
import info.smart_tools.smartactors.core.iresolve_dependency_strategy.IResolveDependencyStrategy;

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
     * @param <T> type of returned value of inserted key
     * @return instance of {@link IKey}
     */
    public static <T> IKey<IKey<T>> getKeyForKeyStorage() {
        return container.getKeyForKeyStorage();
    }

    /**
     * Resolve dependency by given given {@link IKey} instance and args
     * @param key instance of {@link IKey}
     * @param args needed parameters for resolve dependency
     * @param <T> type of class for resolution
     * @return instance of class with classId identifier
     * @throws ResolutionException if resolution is impossible because of any errors
     */
    public static <T> T resolve(final IKey<T> key, final Object ... args)
            throws ResolutionException {
        return container.resolve(key, args);
    }

    /**
     * Register new dependency by instance of {@link IKey}
     * @param key instance of {@link IKey}
     * @param strategy instance of {@link IResolveDependencyStrategy}
     * @throws RegistrationException when registration is impossible because of any error
     */
    public static void register(final IKey key, final IResolveDependencyStrategy strategy)
            throws RegistrationException {
        container.register(key, strategy);
    }

    /**
     * Remove dependency with given key
     * @param key instance of {@link IKey}
     * @throws DeletionException if any errors occurred
     */
    public static void remove(final IKey key)
            throws DeletionException {
        container.remove(key);
    }
}
