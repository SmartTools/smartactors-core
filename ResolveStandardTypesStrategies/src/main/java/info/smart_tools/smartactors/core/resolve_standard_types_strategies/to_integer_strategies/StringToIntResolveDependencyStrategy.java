package info.smart_tools.smartactors.core.resolve_standard_types_strategies.to_integer_strategies;

import info.smart_tools.smartactors.core.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.core.iresolve_dependency_strategy.exception.ResolveDependencyStrategyException;

/**
 * Strategy for resolve Integer from String
 */
public class StringToIntResolveDependencyStrategy implements IResolveDependencyStrategy {
    @Override
    public <T> T resolve(final Object... args) throws ResolveDependencyStrategyException {
        try {
            return (T) new Integer((String) args[0]);
        } catch (Exception e) {
            throw new ResolveDependencyStrategyException(e);
        }
    }
}
