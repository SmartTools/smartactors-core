package info.smart_tools.smartactors.version_management.versioned_recursive_strategy_container;

import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.class_management.interfaces.imodule.IModule;
import info.smart_tools.smartactors.class_management.module_manager.ModuleManager;
import info.smart_tools.smartactors.ioc.istrategy_container.IStrategyContainer;
import info.smart_tools.smartactors.ioc.istrategy_container.exception.StrategyContainerException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of {@link IStrategyContainer}
 * Simple key-value storage
 * <ul>
 *     <li>key is a unique object identifier</li>
 *     <li>value is a instance of {@link IStrategy}</li>
 * </ul>
 * Stores the link to the parent container to make the recursive resolving
 * when the strategy doesn't exist in the current container.
 */
public class StrategyContainer implements IStrategyContainer {

    private IStrategyContainer parent;
    /**
     * Local storage
     */
    private Map<Object, Map<IModule, IStrategy>> strategyStorage = new ConcurrentHashMap<>();

    /**
     *  Constructs the container.
     *  @param parent   parent container to do the recursive resolve, can be null for empty parent
     */
    public StrategyContainer(final IStrategyContainer parent) {
        this.parent = parent;
    }

    /**
     * Resolve {@link IStrategy} by given unique object identifier.
     * Asks parent strategy if this container doesn't have a strategy for the key.
     * @param key unique object identifier
     * @return instance of {@link IStrategy}
     * @throws StrategyContainerException if any errors occurred
     */
    public IStrategy resolve(final Object key)
            throws StrategyContainerException {
        IStrategy strategy = null;
        Map<IModule, IStrategy> strategyVersions = strategyStorage.get(key);
        if (strategyVersions != null) {
            strategy = ModuleManager.getFromMap(strategyVersions);
        }
        if (strategy == null && parent != null) {
            strategy = parent.resolve(key);    // ask parent ONLY AFTER local resolution failed
        }
        return strategy;
    }

    /**
     * Register new dependency of {@link IStrategy} instance by unique object identifier
     * @param key unique object identifier
     * @param strategy instance of {@link IStrategy}
     * @throws StrategyContainerException if any error occurred
     */
    public void register(final Object key, final IStrategy strategy)
            throws StrategyContainerException {
        Map<IModule, IStrategy> strategyVersions = strategyStorage.get(key);
        if (strategyVersions == null) {
            strategyVersions = new ConcurrentHashMap<>();
            strategyStorage.put(key, strategyVersions);
        }
        ModuleManager.putToMap(strategyVersions, strategy);
    }

    /**
     * Remove existing dependency of {@link IStrategy} by unique object identifier.
     * Note that unregister is done only for this container,
     * the following call to {@link #resolve(Object)} may return the strategy from the parent container.
     * @param key unique object identifier
     * @throws StrategyContainerException  if any error occurred
     */
    public IStrategy unregister(final Object key)
            throws StrategyContainerException {
        IStrategy strategy = null;
        Map<IModule, IStrategy> strategyVersions = strategyStorage.get(key);
        if (strategyVersions != null) {
            strategy = ModuleManager.removeFromMap(strategyVersions);
            if (strategyVersions.size() == 0) {
                strategyStorage.remove(key);
            }
        }
        return strategy;
    }
}
