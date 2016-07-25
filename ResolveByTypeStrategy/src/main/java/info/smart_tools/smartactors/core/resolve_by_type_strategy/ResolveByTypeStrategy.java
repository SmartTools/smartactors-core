package info.smart_tools.smartactors.core.resolve_by_type_strategy;

import info.smart_tools.smartactors.core.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.core.iresolve_dependency_strategy.exception.ResolveDependencyStrategyException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Strategy for resolving by type
 */
public class ResolveByTypeStrategy implements IResolveDependencyStrategy {
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
    public void register(final Class key, final IResolveDependencyStrategy strategy) {
        resolveStrategies.put(key, strategy);
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
            throw new ResolveDependencyStrategyException("Object resolution failed.", e);
        }
    }
}
