package info.smart_tools.smartactors.core.resolve_standard_types_strategies.to_bigdecimal;

import info.smart_tools.smartactors.core.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.core.iresolve_dependency_strategy.exception.ResolveDependencyStrategyException;

import java.math.BigDecimal;

/**
 * from double to bigdecimal
 */
public class DoubleToBigDecimalResolveDependencyStrategy implements IResolveDependencyStrategy {
    @Override
    public <T> T resolve(final Object... args) throws ResolveDependencyStrategyException {
        try {
            return (T) BigDecimal.valueOf((Double) args[0]);
        } catch (Exception e) {
            throw new ResolveDependencyStrategyException(e);
        }
    }
}
