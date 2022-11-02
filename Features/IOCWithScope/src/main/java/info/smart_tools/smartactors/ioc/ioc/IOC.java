package info.smart_tools.smartactors.ioc.ioc;

import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.ioc.exception.DeletionException;
import info.smart_tools.smartactors.ioc.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.istrategy_container.IStrategyContainer;
import info.smart_tools.smartactors.ioc.string_ioc_key.Key;
import info.smart_tools.smartactors.scope.scope_provider.ScopeProvider;

/**
 * Realization of IOC Container by ServiceLocator pattern
 */
public final class IOC {

    /** Key for getting instance of {@link IStrategyContainer} from current scope */
    private static IKey strategyContainerKey;
    /** */
    private static IKey keyByNameStrategyKey;

    static {
        try {
            strategyContainerKey = new Key(java.util.UUID.randomUUID().toString());
            keyByNameStrategyKey = new Key(java.util.UUID.randomUUID().toString());
        } catch (Exception e) {
            throw new RuntimeException("IOC initialization has been failed.");
        }
    }

    /**
     * Return specific instance of {@link IKey} for container ID
     * @return instance of {@link IKey}
     */
    public static IKey getIocKey() {
        return strategyContainerKey;
    }

    /**
     * Return specific instance of {@link IKey} for strategy of resolving key by name
     * @return instance of {@link IKey}
     */
    public static IKey getKeyForKeyByNameStrategy() {
        return keyByNameStrategyKey;
    }

    /**
     * Resolve dependency by given given {@link IKey} instance and args
     * @param key instance of {@link IKey}
     * @param args needed parameters for resolve dependency
     * @param <T> type of class for resolution
     * @return instance of class with classId identifier
     * @throws ResolutionException if resolution is impossible because of any errors
     */
    @SuppressWarnings("unchecked")
    public static <T> T resolve(final IKey key, final Object ... args)
            throws ResolutionException {
        try {
            IStrategyContainer strategyContainer = (IStrategyContainer) ScopeProvider.getCurrentScope().getValue(strategyContainerKey);
            IStrategy strategy = strategyContainer.resolve(key);

            return (T) strategy.resolve(args);
        } catch (Throwable e) {
            throw new ResolutionException("Resolution of dependency failed for key '" + key + "'.", e);
        }
    }

    /**
     * Register new dependency by instance of {@link IKey}
     * @param key instance of {@link IKey}
     * @param strategy instance of {@link IStrategy}
     * @throws RegistrationException when registration is impossible because of any error
     */
    public static void register(final IKey key, final IStrategy strategy)
            throws RegistrationException {
        try {
            IStrategyContainer strategyContainer = (IStrategyContainer) ScopeProvider
                    .getCurrentScope()
                    .getValue(strategyContainerKey);
            strategyContainer.register(key, strategy);
        } catch (Throwable e) {
            throw new RegistrationException("Registration of dependency failed for key " + key, e);
        }
    }

    /**
     * Unregister dependency with given key
     * @param key instance of {@link IKey}
     * @throws DeletionException if any errors occurred
     * @return the previous instance of {@link IStrategy} associated with {@code key},
     *         or {@code null} if there was no association for {@code key}.
     */
    public static IStrategy unregister(final IKey key)
            throws DeletionException {
        try {
            IStrategyContainer strategyContainer = (IStrategyContainer) ScopeProvider
                    .getCurrentScope()
                    .getValue(strategyContainerKey);
            return strategyContainer.unregister(key);
        } catch (Throwable e) {
            throw new DeletionException("Deletion of dependency failed for key " + key, e);
        }
    }
}
