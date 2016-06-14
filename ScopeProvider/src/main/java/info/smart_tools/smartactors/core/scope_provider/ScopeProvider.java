package info.smart_tools.smartactors.core.scope_provider;

import info.smart_tools.smartactors.core.iaction.IAction;
import info.smart_tools.smartactors.core.iscope.IScope;
import info.smart_tools.smartactors.core.iscope.IScopeFactory;
import info.smart_tools.smartactors.core.iscope_provider_container.IScopeProviderContainer;
import info.smart_tools.smartactors.core.iscope_provider_container.exception.ScopeProviderException;
import info.smart_tools.smartactors.core.recursive_scope.ScopeFactory;
import info.smart_tools.smartactors.core.scope_provider_container.ScopeProviderContainer;

/**
 * Realization of ScopeProvider by ServiceLocator pattern
 */
public final class ScopeProvider {

    /**
     * Implementation of {@link IScopeProviderContainer}.
     * Will be initialized by default implementation of {@link IScopeProviderContainer}
     * ReInitialization possible only with using java reflection API
     * Example:
     * <pre>
     * {@code
     * Field field = ScopeProvider.class.getDeclaredField("container");
     * field.setAccessible(true);
     * field.set(null, new Object());
     * field.setAccessible(false);
     * }
     * </pre>
     */
    private static IScopeProviderContainer container;

    /**
     * Default private constructor
     */
    private ScopeProvider() {
    }

    static {
        IScopeFactory factory = new ScopeFactory();
        container = new ScopeProviderContainer(factory);
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
        return container.getScope(key);
    }

    /**
     * Get current instance of {@link IScope}
     * @return instance of {@link IScope}
     * @throws ScopeProviderException if current scope is null or any errors occurred
     */
    public static IScope getCurrentScope()
            throws ScopeProviderException {
        return container.getCurrentScope();
    }

    /**
     * Add new dependency to the local storage of {@link IScopeProviderContainer} by unique identifier
     * @param key unique identifier
     * @param scope instance of {@link IScope}
     * @throws ScopeProviderException if any errors occurred
     */
    public static void addScope(final Object key, final IScope scope)
            throws ScopeProviderException {
        container.addScope(key, scope);
    }

    /**
     * Set given instance of {@link IScope} as current to the {@link IScopeProviderContainer}
     * @param scope instance of {@link IScope}
     * @throws ScopeProviderException if any errors occurred
     */
    public static void setCurrentScope(final IScope scope)
            throws ScopeProviderException {
        container.setCurrentScope(scope);
    }

    /**
     * Remove given instance of {@link IScope} from local storage of {@link IScopeProviderContainer} by unique identifier
     * @param key unique identifier {@link IScope} instance
     * @throws ScopeProviderException if any errors occurred
     */
    public static void deleteScope(final Object key)
            throws ScopeProviderException {
        container.deleteScope(key);
    }

    /**
     * Create new instance of {@link IScope} and store it to the local storage of {@link IScopeProviderContainer}
     * @param param needed parameters for creation instance of {@link IScope}
     * @return unique identifier of created instance of {@link IScope}
     * @throws ScopeProviderException if any errors occurred
     */
    public static Object createScope(final Object param)
            throws ScopeProviderException {
        return container.createScope(param);
    }

    /**
     * Register event handler (action) to the local action storage
     * @param handler handler for execute when event will be happened
     * @throws ScopeProviderException if any errors occurred
     */
    public static void subscribeOnCreationNewScope(final IAction<IScope> handler)
            throws ScopeProviderException {
        container.subscribeOnCreationNewScope(handler);
    }

    /**
     * Clear local action storage
     * @throws ScopeProviderException if any errors occurred
     */
    public static void clearListOfSubscribers()
            throws ScopeProviderException {
        container.clearListOfSubscribers();
    }
}
