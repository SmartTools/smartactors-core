package info.smart_tools.smartactors.ioc.ioc_container_simple;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.ioc.iioccontainer.IContainer;
import info.smart_tools.smartactors.ioc.exception.DeletionException;
import info.smart_tools.smartactors.ioc.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.string_ioc_key.Key;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Basic default implementation of {@link IContainer}
 * <pre>
 * Implementation features:
 * - fast and simple
 * </pre>
 */
public class Container implements IContainer {

    private final Map<IKey, IStrategy> storage = new ConcurrentHashMap<>();

    private final IKey keyByNameStrategyKey;

    /**
     * The constructor.
     *
     * @throws InvalidArgumentException if fails to construct key storage
     */
    public Container()
            throws InvalidArgumentException {
        keyByNameStrategyKey = new Key(UUID.randomUUID().toString());
    }

    /**
     * Return specific instance of {@link IKey} for container ID
     *
     * @return instance of {@link IKey}
     */
    @Override
    public IKey getIocKey() {
        return null;
    }

    /**
     * Return specific instance of {@link IKey} for strategy of resolving key by name
     * @return instance of {@link IKey}
     */
    @Override
    public IKey getKeyForKeyByNameStrategy() {
        return keyByNameStrategyKey;
    }

    /**
     * Resolve dependency by given given {@link IKey} instance and args
     *
     * @param key  instance of {@link IKey}
     * @param args needed parameters for resolve dependency
     * @param <T>  type of class for resolution
     * @return instance of class with classId identifier
     * @throws ResolutionException if resolution is impossible because of any errors
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T resolve(final IKey key, final Object... args)
            throws ResolutionException {
        if (key == null) {
            throw new ResolutionException("Key can't be null");
        }
        IStrategy strategy = storage.get(key);
        if (strategy == null) {
            throw new ResolutionException("Strategy for key " + key + " not found");
        }

        try {
            return (T) strategy.resolve(args);
        } catch (Exception e) {
            throw new ResolutionException("Resolution of dependency failed for key '" + key + "'.", e);
        }
    }

    /**
     * Register new dependency by instance of {@link IKey}
     *
     * @param key      instance of {@link IKey}
     * @param strategy instance of {@link IStrategy}
     * @throws RegistrationException when registration is impossible because of any error
     */
    @Override
    public void register(final IKey key, final IStrategy strategy)
            throws RegistrationException {
        if (key == null) {
            throw new RegistrationException("Key can't be null");
        }
        if (strategy == null) {
            throw new RegistrationException("Strategy can't be null");
        }

        try {
            storage.put(key, strategy);
        } catch (Exception e) {
            throw new RegistrationException("Registration of dependency failed for key " + key, e);
        }
    }

    /**
     * Remove dependency with given key
     *
     * @param key instance of {@link IKey}
     * @throws DeletionException if any errors occurred
     */
    @Override
    public IStrategy unregister(final IKey key)
            throws DeletionException {
        if (key == null) {
            throw new DeletionException("Key can't be null");
        }
        try {
            return storage.remove(key);
        } catch (Exception e) {
            throw new DeletionException("Deletion of dependency failed for key " + key, e);
        }
    }
}
