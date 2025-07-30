package info.smart_tools.smartactors.ioc_strategy_pack.resolve_standard_types_strategies.to_long;

import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.interfaces.istrategy.exception.StrategyException;

/**
 * from integer to long
 */
public class IntegerToLongStrategy implements IStrategy {

    @Override
    public <T> T resolve(final Object... args) throws StrategyException {
        try {
            return (T) Long.valueOf((Integer) args[0]);
        } catch (Exception e) {
            throw new StrategyException(e);
        }
    }
}
