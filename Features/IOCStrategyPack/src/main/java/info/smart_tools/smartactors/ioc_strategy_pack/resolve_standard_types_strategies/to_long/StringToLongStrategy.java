package info.smart_tools.smartactors.ioc_strategy_pack.resolve_standard_types_strategies.to_long;

import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.interfaces.istrategy.exception.StrategyException;

/**
 * from string to long
 */
public class StringToLongStrategy implements IStrategy {

    @Override
    public <T> T resolve(final Object... args) throws StrategyException {
        try {
            return (T) Long.valueOf((String) args[0]);
        } catch (Exception e) {
            throw new StrategyException(e);
        }
    }
}
