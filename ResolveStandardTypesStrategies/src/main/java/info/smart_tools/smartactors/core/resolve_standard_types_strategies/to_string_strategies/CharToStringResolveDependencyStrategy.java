package info.smart_tools.smartactors.core.resolve_standard_types_strategies.to_string_strategies;

import info.smart_tools.smartactors.core.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.core.iresolve_dependency_strategy.exception.ResolveDependencyStrategyException;

/**
 * from char primitive to string
 */
public class CharToStringResolveDependencyStrategy implements IResolveDependencyStrategy {
    @Override
    public <T> T resolve(final Object... args) throws ResolveDependencyStrategyException {
        return (T) String.valueOf((char) args[0]);
    }
}