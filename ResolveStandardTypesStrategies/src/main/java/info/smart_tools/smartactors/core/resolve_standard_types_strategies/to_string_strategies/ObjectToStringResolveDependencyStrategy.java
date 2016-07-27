package info.smart_tools.smartactors.core.resolve_standard_types_strategies.to_string_strategies;

import info.smart_tools.smartactors.core.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.core.iresolve_dependency_strategy.exception.ResolveDependencyStrategyException;

/**
 * from any object to string
 */
public class ObjectToStringResolveDependencyStrategy implements IResolveDependencyStrategy {
    @Override
    public <T> T resolve(final Object... args) throws ResolveDependencyStrategyException {
        try {
            return (T) args[0].toString();
        } catch (Exception e) {
            throw new ResolveDependencyStrategyException(e);
        }
    }
}
