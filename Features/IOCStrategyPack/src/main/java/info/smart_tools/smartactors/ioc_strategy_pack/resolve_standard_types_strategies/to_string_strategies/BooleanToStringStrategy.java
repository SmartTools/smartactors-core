package info.smart_tools.smartactors.ioc_strategy_pack.resolve_standard_types_strategies.to_string_strategies;

import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.interfaces.istrategy.exception.StrategyException;

/**
 * from boolean primitive to string
 */
public class BooleanToStringStrategy implements IStrategy {
    @Override
    public <T> T resolve(final Object... args) throws StrategyException {
        try {
            return (T) String.valueOf((boolean) args[0]);
        } catch (Exception e) {
            throw new StrategyException(e);
        }
    }
}
