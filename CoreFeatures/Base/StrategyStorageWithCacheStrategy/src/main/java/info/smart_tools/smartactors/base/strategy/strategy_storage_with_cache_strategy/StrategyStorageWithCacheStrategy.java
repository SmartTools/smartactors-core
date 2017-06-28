package info.smart_tools.smartactors.base.strategy.strategy_storage_with_cache_strategy;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.i_addition_dependency_strategy.IAdditionDependencyStrategy;
import info.smart_tools.smartactors.base.interfaces.i_addition_dependency_strategy.exception.AdditionDependencyStrategyException;
import info.smart_tools.smartactors.base.interfaces.iaction.IBiFunction;
import info.smart_tools.smartactors.base.interfaces.iaction.IFunction;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.FunctionExecutionException;
import info.smart_tools.smartactors.base.interfaces.icacheable.ICacheable;
import info.smart_tools.smartactors.base.interfaces.icacheable.exception.DropCacheException;
import info.smart_tools.smartactors.base.interfaces.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.base.interfaces.iresolve_dependency_strategy.exception.ResolveDependencyStrategyException;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Strategy to strategyStorage some specific strategies united by a common purpose. Supports cache.
 */

public class StrategyStorageWithCacheStrategy implements IResolveDependencyStrategy, IAdditionDependencyStrategy, ICacheable {

    /**
     * Specific strategies for resolve
     */
    private ConcurrentMap<Object, IResolveDependencyStrategy> strategyStorage;
    private ConcurrentMap<Object, IResolveDependencyStrategy> cacheStrategiesMap;
    private IFunction argToKeyFunction;
    private IBiFunction findValueByArgumentFunction;


    /**
     *
     * @param argToKeyFunction the function that transforms argument to a map key
     * @param findValueByArgumentFunction the function that finds value by given argument in the given map
     */
    public StrategyStorageWithCacheStrategy(final IFunction argToKeyFunction, final IBiFunction findValueByArgumentFunction) {
        this.strategyStorage = new ConcurrentHashMap<>();
        this.cacheStrategiesMap = new ConcurrentHashMap<>();
        this.argToKeyFunction = argToKeyFunction;
        this.findValueByArgumentFunction = findValueByArgumentFunction;
    }


    @Override
    public <T> T resolve(Object... args)
            throws ResolveDependencyStrategyException {
        try {
            Object key = this.argToKeyFunction.execute(args[0]);
            IResolveDependencyStrategy strategy = this.cacheStrategiesMap.get(key);
            if (null != strategy) {
                return strategy.resolve(args[0]);
            }

            strategy = (IResolveDependencyStrategy) this.findValueByArgumentFunction.execute(this.strategyStorage, args[0]);

            if (null == strategy) {
                throw new ResolveDependencyStrategyException("No strategy found for " + args[0]);
            }

            this.cacheStrategiesMap.put(key, strategy);

            return strategy.resolve(args[0]);
        } catch (RuntimeException | FunctionExecutionException | InvalidArgumentException e) {
            throw new ResolveDependencyStrategyException(e);
        }
    }

    @Override
    public void register(Object key, IResolveDependencyStrategy value)
            throws AdditionDependencyStrategyException {
        try {
            this.dropCacheFor(key);
            this.strategyStorage.put(key, value);
        } catch (DropCacheException e) {
            throw new AdditionDependencyStrategyException(e);
        }
    }

    @Override
    public void remove(Object key)
            throws AdditionDependencyStrategyException {
        try {
            this.dropCacheFor(key);
            this.strategyStorage.remove(key);
        } catch (DropCacheException e) {
            throw new AdditionDependencyStrategyException(e);
        }
    }

    @Override
    public void dropCache()
            throws DropCacheException {
        this.cacheStrategiesMap.clear();
    }

    @Override
    public void dropCacheFor(final Object key)
            throws DropCacheException {
        try {
            this.cacheStrategiesMap.remove(key);
        } catch (Throwable e) {
            throw new DropCacheException(e);
        }
    }
}
