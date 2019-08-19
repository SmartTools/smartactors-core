package info.smart_tools.smartactors.base.strategy.strategy_storage_with_cache_strategy;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.istrategy_registration.IStrategyRegistration;
import info.smart_tools.smartactors.base.interfaces.istrategy_registration.exception.StrategyRegistrationException;
import info.smart_tools.smartactors.base.interfaces.iaction.IFunctionTwoArgs;
import info.smart_tools.smartactors.base.interfaces.iaction.IFunction;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.FunctionExecutionException;
import info.smart_tools.smartactors.base.interfaces.icacheable.ICacheable;
import info.smart_tools.smartactors.base.interfaces.icacheable.exception.CacheDropException;
import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.interfaces.istrategy.exception.StrategyException;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Strategy to strategyStorage some specific strategies united by a common purpose. Supports cache.
 */

public class StrategyStorageWithCacheStrategy implements IStrategy, IStrategyRegistration, ICacheable {

    /**
     * Specific strategies for resolve
     */
    private ConcurrentMap<Object, IStrategy> strategyStorage;
    private ConcurrentMap<Object, IStrategy> cacheStrategiesMap;
    private IFunction argToKeyFunction;
    private IFunctionTwoArgs findValueByArgumentFunction;


    /**
     *
     * @param argToKeyFunction the function that transforms argument to a map key
     * @param findValueByArgumentFunction the function that finds value by given argument in the given map
     */
    public StrategyStorageWithCacheStrategy(final IFunction argToKeyFunction, final IFunctionTwoArgs findValueByArgumentFunction) {
        this.strategyStorage = new ConcurrentHashMap<>();
        this.cacheStrategiesMap = new ConcurrentHashMap<>();
        this.argToKeyFunction = argToKeyFunction;
        this.findValueByArgumentFunction = findValueByArgumentFunction;
    }


    @Override
    @SuppressWarnings("unchecked")
    public <T> T resolve(final Object... args)
            throws StrategyException {
        try {
            Object key = this.argToKeyFunction.execute(args[0]);
            IStrategy strategy = this.cacheStrategiesMap.get(key);
            if (null != strategy) {
                return strategy.resolve(args[0]);
            }

            strategy = (IStrategy) this.findValueByArgumentFunction.execute(this.strategyStorage, args[0]);

            if (null == strategy) {
                throw new StrategyException("No strategy found for " + args[0]);
            }

            this.cacheStrategiesMap.put(key, strategy);

            return strategy.resolve(args[0]);
        } catch (RuntimeException | FunctionExecutionException | InvalidArgumentException e) {
            throw new StrategyException(e);
        }
    }

    @Override
    public void register(final Object key, final IStrategy value)
            throws StrategyRegistrationException {
        try {
            this.dropCacheFor(key);
            this.strategyStorage.put(key, value);
        } catch (CacheDropException e) {
            throw new StrategyRegistrationException(e);
        }
    }

    @Override
    public IStrategy unregister(final Object key)
            throws StrategyRegistrationException {
        try {
            this.dropCacheFor(key);
            return this.strategyStorage.remove(key);
        } catch (CacheDropException e) {
            throw new StrategyRegistrationException(e);
        }
    }

    @Override
    public void dropCache()
            throws CacheDropException {
        this.cacheStrategiesMap.clear();
    }

    @Override
    public void dropCacheFor(final Object key)
            throws CacheDropException {
        try {
            this.cacheStrategiesMap.remove(key);
        } catch (Throwable e) {
            throw new CacheDropException(e);
        }
    }
}
