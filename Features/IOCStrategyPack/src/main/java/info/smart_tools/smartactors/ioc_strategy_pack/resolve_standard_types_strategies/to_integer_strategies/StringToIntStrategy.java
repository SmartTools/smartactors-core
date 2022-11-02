package info.smart_tools.smartactors.ioc_strategy_pack.resolve_standard_types_strategies.to_integer_strategies;

import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.interfaces.istrategy.exception.StrategyException;

/**
 * Strategy for resolve Integer from String
 */
public class StringToIntStrategy implements IStrategy {
    @Override
    public <T> T resolve(final Object... args) throws StrategyException {
        try {
            return (T) new Integer((String) args[0]);
        } catch (Exception e) {
            throw new StrategyException(e);
        }
    }
}
