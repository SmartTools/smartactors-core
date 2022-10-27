package info.smart_tools.smartactors.scope.scope_provider;

import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.scope.iscope.IScope;
import info.smart_tools.smartactors.scope.iscope.IScopeFactory;
import info.smart_tools.smartactors.scope.iscope_provider_container.IScopeProviderContainer;
import info.smart_tools.smartactors.scope.iscope_provider_container.exception.ScopeProviderException;
import info.smart_tools.smartactors.scope.recursive_scope.ScopeFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Realization of ScopeProvider by ServiceLocator pattern
 */
public final class ScopeProvider {

    /**
     * Local storage of all {@link IScope} instances by unique identifier
     */
    private static Map<Object, IScope> scopeStorage = new ConcurrentHashMap<Object, IScope>();

    /**
     * Current instance of {@link IScope} for current thread
     */
    private static ThreadLocal<IScope> currentScope = new ThreadLocal<IScope>();

    /**
     * Instance of {@link IScopeFactory}
     */
    private static IScopeFactory factory;

    /**
     * Local storage for create scope event handlers
     */
    private static List<IAction<IScope>> handlerStorage = new ArrayList<>();


    /**
     * Default private constructor
     */
    private ScopeProvider() {
    }

    static {
        factory = new ScopeFactory();
    }

    /**
     * Get instance of {@link IScope} from local storage of {@link IScopeProviderContainer} by unique
     * identifier of {@link IScope} instance
     * @param key unique identifier of {@link IScope} instance
     * @return instance of {@link IScope}
     * @throws ScopeProviderException if value is not found or any errors occurred
     */
    public static IScope getScope(final Object key)
            throws ScopeProviderException {
        IScope scope = scopeStorage.get(key);
        if (null == scope) {
            throw new ScopeProviderException("Scope not found.");
        }

        return scope;
    }

    /**
     * Get current instance of {@link IScope}
     * @return instance of {@link IScope}
     * @throws ScopeProviderException if current scope is null or any errors occurred
     */
    public static IScope getCurrentScope()
            throws ScopeProviderException {
        IScope scope = currentScope.get();
        if (null == scope) {
            throw new ScopeProviderException("Current Scope is null.");
        }

        return scope;
    }

    /**
     * Add new dependency to the local storage of {@link IScopeProviderContainer} by unique identifier
     * @param key unique identifier
     * @param scope instance of {@link IScope}
     * @throws ScopeProviderException if any errors occurred
     */
    public static void addScope(final Object key, final IScope scope)
            throws ScopeProviderException {
        try {
            scopeStorage.put(key, scope);
        } catch (Exception e) {
            throw new ScopeProviderException("Error was occurred", e);
        }
    }

    /**
     * Set given instance of {@link IScope} as current to the {@link IScopeProviderContainer}
     * @param scope instance of {@link IScope}
     * @throws ScopeProviderException if any errors occurred
     */
    public static void setCurrentScope(final IScope scope)
            throws ScopeProviderException {
        currentScope.set(scope);
    }

    /**
     * Remove given instance of {@link IScope} from local storage of {@link IScopeProviderContainer} by unique identifier
     * @param key unique identifier {@link IScope} instance
     * @throws ScopeProviderException if any errors occurred
     */
    public static void deleteScope(final Object key)
            throws ScopeProviderException {
        try {
            scopeStorage.remove(key);
        } catch (Exception e) {
            throw new ScopeProviderException("Error was occurred", e);
        }
    }

    /**
     * Create new instance of {@link IScope} and store it to the local storage of {@link IScopeProviderContainer}
     * @param params needed parameters for creation instance of {@link IScope}
     * @return unique identifier of created instance of {@link IScope}
     * @throws ScopeProviderException if any errors occurred
     */
    public static Object createScope(final Object params)
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
    public static void subscribeOnCreationNewScope(final IAction<IScope> handler)
            throws ScopeProviderException {
        if (null == handler) {
            throw new ScopeProviderException("Incoming argument should not be null");
        }
        handlerStorage.add(handler);
    }

    /**
     * Clear local action storage
     * @throws ScopeProviderException if any errors occurred
     */
    public static void clearListOfSubscribers()
            throws ScopeProviderException {
        handlerStorage.clear();
    }
}
