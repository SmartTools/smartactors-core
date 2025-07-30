package info.smart_tools.smartactors.ioc_strategy_pack.resolve_standard_types_strategies.to_integer_strategies;

import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.interfaces.istrategy.exception.StrategyException;

/**
 * from double to integer
 */
public class DoubleToIntStrategy implements IStrategy {
    @Override
    public <T> T resolve(final Object... args) throws StrategyException {
        try {
            return (T) Integer.valueOf(((Double) args[0]).intValue());
        } catch (Exception e) {
            throw new StrategyException(e);
        }
    }
}
