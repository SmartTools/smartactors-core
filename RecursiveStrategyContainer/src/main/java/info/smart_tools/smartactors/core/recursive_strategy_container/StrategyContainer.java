package info.smart_tools.smartactors.core.recursive_strategy_container;

import info.smart_tools.smartactors.core.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.core.istrategy_container.IStrategyContainer;
import info.smart_tools.smartactors.core.istrategy_container.exception.StrategyContainerException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of {@link IStrategyContainer}
 * <p>
 * Simple key-value storage
 * <ul>
 *     <li>key is a unique object identifier</li>
 *     <li>value is a instance of {@link IResolveDependencyStrategy}</li>
 * </ul>
 * </p>
 * <p>
 * Stores the link to the parent container to make the recursive resolving
 * when the strategy doesn't exist in the current container.
 * </p>
 */
public class StrategyContainer implements IStrategyContainer {

    private IStrategyContainer parentContainer = new EmptyStrategyContainer();
    /**
     * Local storage
     */
    private Map<Object, IResolveDependencyStrategy> strategyStorage = new ConcurrentHashMap<Object, IResolveDependencyStrategy>();

    /**
     *  Constructs the container.
     *  @param parent   parent container to do the recursive resolve, can be null for empty parent
     */
    public StrategyContainer(final IStrategyContainer parent) {
        if (parent != null) {
            parentContainer = parent;
        }
    }

    /**
     * Resolve {@link IResolveDependencyStrategy} by given unique object identifier.
     * Asks parent strategy if this container doesn't have a strategy for the key.
     * @param key unique object identifier
     * @return instance of {@link IResolveDependencyStrategy}
     * @throws StrategyContainerException if any errors occurred
     */
    public IResolveDependencyStrategy resolve(final Object key)
            throws StrategyContainerException {
        IResolveDependencyStrategy strategy = strategyStorage.get(key);
        if (strategy == null) {
            strategy = parentContainer.resolve(key);    // ask parent ONLY AFTER local resolution failed
        }
        return strategy;
    }

    /**
     * Register new dependency of {@link IResolveDependencyStrategy} instance by unique object identifier
     * @param key unique object identifier
     * @param strategy instance of {@link IResolveDependencyStrategy}
     * @throws StrategyContainerException if any error occurred
     */
    public void register(final Object key, final IResolveDependencyStrategy strategy)
            throws StrategyContainerException {
        strategyStorage.put(key, strategy);
    }

    /**
     * Remove existing dependency of {@link IResolveDependencyStrategy} by unique object identifier.
     * Note remove is done only for this container,
     * the following call to {@link #resolve(Object)} may return the strategy from the parent container.
     * @param key unique object identifier
     * @throws StrategyContainerException  if any error occurred
     */
    public void remove(final Object key)
            throws StrategyContainerException {
        strategyStorage.remove(key);
    }
}
