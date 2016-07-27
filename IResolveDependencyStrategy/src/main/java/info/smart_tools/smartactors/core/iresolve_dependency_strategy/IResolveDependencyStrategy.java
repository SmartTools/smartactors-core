package info.smart_tools.smartactors.core.iresolve_dependency_strategy;

import info.smart_tools.smartactors.core.iresolve_dependency_strategy.exception.ResolveDependencyStrategyException;

/**
 * IResolveDependencyStrategy
 */
public interface IResolveDependencyStrategy {

    /**
     * Resolve dependency by realized strategy
     * @param args array of needed parameters for resolve dependency
     * @param <T> type of object
     * @return instance of type T object
     * @throws ResolveDependencyStrategyException if any errors occurred
     */
    <T> T resolve(final Object ... args)
            throws ResolveDependencyStrategyException;
}
