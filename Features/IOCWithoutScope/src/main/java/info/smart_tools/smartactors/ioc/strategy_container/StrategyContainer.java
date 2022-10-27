package info.smart_tools.smartactors.ioc.strategy_container;

import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.ioc.istrategy_container.IStrategyContainer;
import info.smart_tools.smartactors.ioc.istrategy_container.exception.StrategyContainerException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of {@link IStrategyContainer}
 * <pre>
 * Simple key-value storage
 *  - key is a unique object identifier
 *  - value is a instance of {@link IStrategy}
 * </pre>
 */
public class StrategyContainer implements IStrategyContainer {

    /**
     * Local storage
     */
    private Map<Object, IStrategy> strategyStorage = new ConcurrentHashMap<Object, IStrategy>();

    /**
     * Resolve {@link IStrategy} by given unique object identifier
     * @param key unique object identifier
     * @return instance of {@link IStrategy}
     * @throws StrategyContainerException if any errors occurred
     */
    public IStrategy resolve(final Object key)
            throws StrategyContainerException {
        return strategyStorage.get(key);
    }

    /**
     * Register new dependency of {@link IStrategy} instance by unique object identifier
     * @param key unique object identifier
     * @param strategy instance of {@link IStrategy}
     * @throws StrategyContainerException if any error occurred
     */
    public void register(final Object key, final IStrategy strategy)
            throws StrategyContainerException {
        strategyStorage.put(key, strategy);
    }

    /**
     * Remove existing dependency of {@link IStrategy} by unique object identifier
     * @param key unique object identifier
     * @throws StrategyContainerException if any error occurred
     */
    public IStrategy unregister(final Object key)
            throws StrategyContainerException {
        return strategyStorage.remove(key);
    }
}
