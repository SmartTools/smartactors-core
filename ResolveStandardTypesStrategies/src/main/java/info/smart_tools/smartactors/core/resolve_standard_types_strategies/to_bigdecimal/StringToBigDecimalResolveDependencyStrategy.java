package info.smart_tools.smartactors.core.resolve_standard_types_strategies.to_bigdecimal;

import info.smart_tools.smartactors.core.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.core.iresolve_dependency_strategy.exception.ResolveDependencyStrategyException;

import java.math.BigDecimal;

/**
 * from string to BigDecimal
 */
public class StringToBigDecimalResolveDependencyStrategy implements IResolveDependencyStrategy {
    @Override
    public <T> T resolve(final Object... args) throws ResolveDependencyStrategyException {
        try {
            return (T) new BigDecimal((String) args[0]);
        } catch (Exception e) {
            throw new ResolveDependencyStrategyException(e);
        }
    }
}
