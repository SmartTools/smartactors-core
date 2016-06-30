package info.smart_tools.smartactors.core.ioc_container;

import info.smart_tools.smartactors.core.iioccontainer.IContainer;
import info.smart_tools.smartactors.core.iioccontainer.exception.DeletionException;
import info.smart_tools.smartactors.core.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.core.istrategy_container.IStrategyContainer;
import info.smart_tools.smartactors.core.scope_provider.ScopeProvider;
import info.smart_tools.smartactors.core.string_ioc_key.Key;

/**
 * Implementation of {@link IContainer}
 * <pre>
 * Implementation features:
 * - support scopes
 * </pre>
 */
public class Container implements IContainer {

    /** Key for getting instance of {@link IStrategyContainer} from current scope */
    private IKey strategyContainerKey;
    /** */
    private IKey keyForKeyStorage;

    /**
     * Default constructor
     */
    public Container() {
        try {
            strategyContainerKey = new Key(java.util.UUID.randomUUID().toString());
            keyForKeyStorage = new Key(java.util.UUID.randomUUID().toString());
        } catch (Exception e) {
            throw new RuntimeException("Initialization of IOC container has been failed.");
        }
    }

    /**
     * Return specific instance of {@link IKey} for container ID
     * @return instance of {@link IKey}
     */
    @Override
    public IKey getIocKey() {
        return this.strategyContainerKey;
    }

    /**
     * Return specific instance of {@link IKey} for resolve dependencies from key storage
     * @return instance of {@link IKey}
     */
    @Override
    public IKey getKeyForKeyStorage() {
        return this.keyForKeyStorage;
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
    public <T> T resolve(final IKey key, final Object ... args)
            throws ResolutionException {
        try {
            IStrategyContainer strategyContainer = (IStrategyContainer) ScopeProvider.getCurrentScope().getValue(strategyContainerKey);
            IResolveDependencyStrategy strategy = strategyContainer.resolve(key);
            return (T) strategy.resolve(args);
        } catch (Throwable e) {
            throw new ResolutionException("Resolution of dependency failed.", e);
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
            IStrategyContainer strategyContainer = (IStrategyContainer) ScopeProvider.getCurrentScope().getValue(strategyContainerKey);
            strategyContainer.register(key, strategy);
        } catch (Throwable e) {
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
            IStrategyContainer strategyContainer = (IStrategyContainer) ScopeProvider.getCurrentScope().getValue(strategyContainerKey);
            strategyContainer.remove(key);
        } catch (Throwable e) {
            throw new DeletionException("Deletion of dependency failed.", e);
        }
    }
}
