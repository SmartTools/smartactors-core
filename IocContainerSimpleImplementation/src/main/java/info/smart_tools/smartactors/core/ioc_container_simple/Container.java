package info.smart_tools.smartactors.core.ioc_container_simple;

import info.smart_tools.smartactors.core.iioccontainer.IContainer;
import info.smart_tools.smartactors.core.iioccontainer.exception.DeletionException;
import info.smart_tools.smartactors.core.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.iresolve_dependency_strategy.IResolveDependencyStrategy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Basic default implementation of {@link IContainer}
 * <pre>
 * Implementation features:
 * - fast and simple
 * </pre>
 */
public class Container implements IContainer {

    private Map<IKey, IResolveDependencyStrategy> storage = new ConcurrentHashMap<IKey, IResolveDependencyStrategy>();

    /**
     * Return specific instance of {@link IKey} for container ID
     * @return instance of {@link IKey}
     */
    @Override
    public IKey getIocKey() {
        return null;
    }

    /**
     * Return specific instance of {@link IKey} for resolve dependencies from key storage
     * @return instance of {@link IKey}
     */
    @Override
    public IKey<IKey> getKeyForKeyStorage() {
        return null;
    }

    /**
     * Resolve dependency by given given {@link IKey} instance and args
     * @param key instance of {@link IKey}
     * @param args needed parameters for resolve dependency
     * @param <T> type of class for resolution
     * @return instance of class with classId identifier
     * @throws ResolutionException if resolution is impossible because of any errors
     */
    @Override
    public <T> T resolve(final IKey<T> key, final Object ... args)
            throws ResolutionException {
        try {
            IResolveDependencyStrategy strategy = storage.get(key);
            return (T) strategy.resolve(args);
        } catch (Exception e) {
            throw new ResolutionException("Resolution of dependency failed.");
        }
    }

    /**
     * Register new dependency by instance of {@link IKey}
     * @param key instance of {@link IKey}
     * @param strategy instance of {@link IResolveDependencyStrategy}
     * @throws RegistrationException when registration is impossible because of any error
     */
    @Override
    public void register(final IKey key, final IResolveDependencyStrategy strategy)
            throws RegistrationException {
        try {
            storage.put(key, strategy);
        } catch (Exception e) {
            throw new RegistrationException("Registration of dependency failed.", e);
        }
    }

    /**
     *
     * Remove dependency with given key
     * @param key instance of {@link IKey}
     * @throws DeletionException if any errors occurred
     */
    @Override
    public void remove(final IKey key)
            throws DeletionException {
        try {
            storage.remove(key);
        } catch (Exception e) {
            throw new DeletionException("Deletion of dependency failed.", e);
        }
    }
}
