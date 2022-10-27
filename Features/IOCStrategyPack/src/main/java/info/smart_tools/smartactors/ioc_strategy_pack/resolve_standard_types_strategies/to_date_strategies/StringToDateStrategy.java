package info.smart_tools.smartactors.ioc_strategy_pack.resolve_standard_types_strategies.to_date_strategies;

import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.interfaces.istrategy.exception.StrategyException;

import java.time.LocalDateTime;

/**
 * from String to LocalDateTime
 */
public class StringToDateStrategy implements IStrategy {
    @Override
    public <T> T resolve(final Object... args) throws StrategyException {
        try {
            return (T) LocalDateTime.parse((String) args[0]);
        } catch (Exception e) {
            throw new StrategyException(e);
        }
    }
}
