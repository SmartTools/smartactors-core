package info.smart_tools.smartactors.core.strategy_container;

import info.smart_tools.smartactors.core.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.core.istrategy_container.IStrategyContainer;
import info.smart_tools.smartactors.core.istrategy_container.exception.StrategyContainerException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of {@link info.smart_tools.smartactors.core.istrategy_container.IStrategyContainer}
 * <pre>
 * Simple key-value storage
 *  - key is a unique object identifier
 *  - value is a instance of {@link info.smart_tools.smartactors.core.iresolve_dependency_strategy.IResolveDependencyStrategy}
 * </pre>
 */
public class StrategyContainer implements IStrategyContainer {

    /**
     * Local storage
     */
    private Map<Object, IResolveDependencyStrategy> strategyStorage = new ConcurrentHashMap<Object, IResolveDependencyStrategy>();

    /**
     * Resolve {@link IResolveDependencyStrategy} by given unique object identifier
     * @param key unique object identifier
     * @return instance of {@link IResolveDependencyStrategy}
     * @throws StrategyContainerException if any errors occurred
     */
    public IResolveDependencyStrategy resolve(final Object key)
            throws StrategyContainerException {
        return strategyStorage.get(key);
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
     * Remove existing dependency of {@link IResolveDependencyStrategy} by unique object identifier
     * @param key unique object identifier
     * @throws StrategyContainerException if any error occurred
     */
    public void remove(final Object key)
            throws StrategyContainerException {
        strategyStorage.remove(key);
    }
}
