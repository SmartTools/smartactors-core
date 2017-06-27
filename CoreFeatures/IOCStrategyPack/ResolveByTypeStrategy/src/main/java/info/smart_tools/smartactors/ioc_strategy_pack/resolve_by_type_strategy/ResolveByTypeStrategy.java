package info.smart_tools.smartactors.ioc_strategy_pack.resolve_by_type_strategy;

import info.smart_tools.smartactors.base.interfaces.i_addition_dependency_strategy.IAdditionDependencyStrategy;
import info.smart_tools.smartactors.base.interfaces.i_addition_dependency_strategy.exception.AdditionDependencyStrategyException;
import info.smart_tools.smartactors.base.interfaces.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.base.interfaces.iresolve_dependency_strategy.exception.ResolveDependencyStrategyException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Strategy for resolving by type
 *
 * @deprecated use {@link info.smart_tools.smartactors.base.strategy.strategy_storage_with_cache_strategy.StrategyStorageWithCacheStrategy} instead.
 *
 */
@Deprecated
public class ResolveByTypeStrategy implements IResolveDependencyStrategy, IAdditionDependencyStrategy {
    /**
     * Specific strategies for resolve
     */
    private ConcurrentMap<Class, IResolveDependencyStrategy> resolveStrategies;
    private ConcurrentMap<Class, IResolveDependencyStrategy> cacheStrategiesMap;


    /**
     * Default constructor
     */
    public ResolveByTypeStrategy() {
        this.resolveStrategies = new ConcurrentHashMap<>();
        this.cacheStrategiesMap = new ConcurrentHashMap<>();
    }

    /**
     * Put strategy for specific output type
     * @param key the key for output type
     * @param strategy the strategy for specific output type
     */
    @Override
    public void register(final Object key, final IResolveDependencyStrategy strategy) {
        cacheStrategiesMap.remove((Class) key);
        resolveStrategies.put((Class) key, strategy);
    }

    /**
     * Remove strategy for specific output type
     * @param key the key for output type
     */
    @Override
    public void remove(final Object key) throws AdditionDependencyStrategyException {
        resolveStrategies.remove(key);
    }

    @Override
    public <T> T resolve(final Object... args) throws ResolveDependencyStrategyException {
        try {
            IResolveDependencyStrategy strategy = cacheStrategiesMap.get(args[0].getClass());
            if (strategy == null) {
                for (Map.Entry<Class, IResolveDependencyStrategy> entry : resolveStrategies.entrySet()) {
                    if (entry.getKey().isInstance(args[0])) {
                        strategy = entry.getValue();
                        cacheStrategiesMap.put(args[0].getClass(), strategy);
                        break;
                    }
                }
            }

            Object result = strategy.resolve(args[0]);
            return (T) result;
        } catch (Exception e) {
            throw new ResolveDependencyStrategyException("Object resolution failed: " + e.getMessage(), e);
        }
    }
}
