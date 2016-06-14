package info.smart_tools.smartactors.core.scope_provider_container;

import info.smart_tools.smartactors.core.iaction.IAction;
import info.smart_tools.smartactors.core.iscope.IScope;
import info.smart_tools.smartactors.core.iscope.IScopeFactory;
import info.smart_tools.smartactors.core.iscope_provider_container.IScopeProviderContainer;
import info.smart_tools.smartactors.core.iscope_provider_container.exception.ScopeProviderException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of {@link IScopeProviderContainer}
 */
public class ScopeProviderContainer implements IScopeProviderContainer {

    /**
     * Local storage of all {@link IScope} instances by unique identifier
     */
    private Map<Object, IScope> scopeStorage = new ConcurrentHashMap<Object, IScope>();

    /**
     * Current instance of {@link IScope} for current thread
     */
    private ThreadLocal<IScope> currentScope = new ThreadLocal<IScope>();

    /**
     * Instance of {@link IScopeFactory}
     */
    private IScopeFactory factory;

    /**
     * Local storage for create scope event handlers
     */
    private List<IAction<IScope>> handlerStorage = new ArrayList<>();

    /**
     * Constructor with {@link IScopeFactory}
     * @param factory instance of {@link IScopeFactory}
     */
    public ScopeProviderContainer(final IScopeFactory factory) {
        this.factory = factory;
    }

    /**
     * Get {@link IScope} instance from local storage.
     * @param key unique identifier for instance of an object
     * @return instance of {@link IScope}
     * @throws ScopeProviderException if value is not found or any errors occurred
     */
    public IScope getScope(final Object key) throws ScopeProviderException {
        IScope scope = scopeStorage.get(key);
        if (null == scope) {
            throw new ScopeProviderException("Scope not found.");
        }

        return scope;
    }

    /**
     * Get current instance of {@link IScope}
     * @return instance of {@link IScope}
     * @throws ScopeProviderException if any errors occurred
     */
    public IScope getCurrentScope()
            throws ScopeProviderException {
        IScope scope = currentScope.get();
        if (null == scope) {
            throw new ScopeProviderException("Current Scope is null.");
        }

        return scope;
    }

    /**
     * Put {@link IScope} instance to the local storage
     * @param key unique identifier for instance of an object
     * @param scope instance of {@link IScope}
     * @throws ScopeProviderException if any errors occurred
     */
    public void addScope(final Object key, final IScope scope)
            throws ScopeProviderException {
        try {
            scopeStorage.put(key, scope);
        } catch (Exception e) {
            throw new ScopeProviderException("Error was occurred", e);
        }
    }

    /**
     * Set instance of {@link IScope} as current scope
     * @param scope instance of {@link IScope}
     * @throws ScopeProviderException if any errors occurred
     */
    public void setCurrentScope(final IScope scope)
            throws ScopeProviderException {
        currentScope.set(scope);
    }

    /**
     * Delete {@link IScope} instance from local storage by given key
     * @param key unique identifier for instance of an object
     * @throws ScopeProviderException if any errors occurred
     */
    public void deleteScope(final Object key)
            throws ScopeProviderException {
        try {
            scopeStorage.remove(key);
        } catch (Exception e) {
            throw new ScopeProviderException("Error was occurred", e);
        }
    }

    /**
     * Create new instance of {@link IScope} and put it to the local storage
     * @param params needed parameters for creation
     * @return unique instance of {@link IScope} identifier
     * @throws ScopeProviderException if any errors occurred
     */
    public Object createScope(final Object params)
            throws ScopeProviderException {
        try {
            IScope newScope = factory.createScope(params);
            for (IAction<IScope> handler : handlerStorage) {
                handler.execute(newScope);
            }
            Object uuid = UUID.randomUUID();
            scopeStorage.put(uuid, newScope);

            return uuid;
        } catch (Throwable e) {
            throw new ScopeProviderException("Failed to create instance of IScope.", e);
        }
    }

    /**
     * Register event handler (action) to the local action storage
     * @param handler handler for execute when event will be happened
     * @throws ScopeProviderException if any errors occurred
     */
    @Override
    public void subscribeOnCreationNewScope(final IAction<IScope> handler)
            throws ScopeProviderException {
        if (null == handler) {
            throw new ScopeProviderException("Incoming argument should not be null");
        }
        handlerStorage.add(handler);
    }

    /**
     * Clear local action storage
     */
    @Override
    public void clearListOfSubscribers() {
        handlerStorage.clear();
    }
}
