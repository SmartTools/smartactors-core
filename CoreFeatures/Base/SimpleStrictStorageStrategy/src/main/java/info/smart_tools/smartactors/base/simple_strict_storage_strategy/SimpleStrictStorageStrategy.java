package info.smart_tools.smartactors.base.simple_strict_storage_strategy;

import info.smart_tools.smartactors.base.interfaces.i_addition_dependency_strategy.IAdditionDependencyStrategy;
import info.smart_tools.smartactors.base.interfaces.i_addition_dependency_strategy.exception.AdditionDependencyStrategyException;
import info.smart_tools.smartactors.base.interfaces.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.base.interfaces.iresolve_dependency_strategy.exception.ResolveDependencyStrategyException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple strategy storage that uses a map to choose a concrete strategy.
 *
 * <p>
 *  This strategy will throw when there is no strategy for required key that's why it is called "strict".
 * </p>
 */
public class SimpleStrictStorageStrategy implements IResolveDependencyStrategy, IAdditionDependencyStrategy {
    private final Map<Object, IResolveDependencyStrategy> strategyMap;
    private final String resultName;

    /**
     * The constructor.
     *
     * @param strategyMap map to store strategies
     * @param resultName  string that describes a resulting objects type (used for exception messages)
     */
    public SimpleStrictStorageStrategy(
            final Map<Object, IResolveDependencyStrategy> strategyMap,
            final String resultName) {
        this.strategyMap = strategyMap;
        this.resultName = resultName;
    }

    /**
     * The constructor.
     *
     * @param resultName string that describes a resulting objects type (used for exception messages)
     */
    public SimpleStrictStorageStrategy(final String resultName) {
        this(new ConcurrentHashMap<>(), resultName);
    }

    @Override
    public void register(final Object key, final IResolveDependencyStrategy value)
            throws AdditionDependencyStrategyException {
        if (null == value) {
            throw new AdditionDependencyStrategyException("Strategy should not be null.");
        }

        strategyMap.put(key, value);
    }

    @Override
    public void remove(final Object key)
            throws AdditionDependencyStrategyException {
        strategyMap.remove(key);
    }

    @Override
    public <T> T resolve(final Object... args)
            throws ResolveDependencyStrategyException {
        if (args.length < 1) {
            throw new ResolveDependencyStrategyException("No arguments provided.");
        }

        IResolveDependencyStrategy strategy = strategyMap.get(args[0]);

        if (null == strategy) {
            throw new ResolveDependencyStrategyException("No " + resultName + " resolution strategy found for key '" + args[0] + "'.");
        }

        try {
            return strategy.resolve(args);
        } catch (Exception e) {
            throw new ResolveDependencyStrategyException("Error occurred resolving " + resultName + " for key '" + args[0] + "'.", e);
        }
    }
}
