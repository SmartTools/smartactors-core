package info.smart_tools.smartactors.core.resolve_standard_types_strategies.to_integer_strategies;

import info.smart_tools.smartactors.core.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.core.iresolve_dependency_strategy.exception.ResolveDependencyStrategyException;

/**
 * from double to integer
 */
public class DoubleToIntResolveDependencyStrategy implements IResolveDependencyStrategy {
    @Override
    public <T> T resolve(final Object... args) throws ResolveDependencyStrategyException {
        try {
            return (T) Integer.valueOf(((Double) args[0]).intValue());
        } catch (Exception e) {
            throw new ResolveDependencyStrategyException(e);
        }
    }
}
