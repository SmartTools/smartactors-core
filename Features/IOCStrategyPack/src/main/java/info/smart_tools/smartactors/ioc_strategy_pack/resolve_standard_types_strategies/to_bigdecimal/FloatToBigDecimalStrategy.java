package info.smart_tools.smartactors.ioc_strategy_pack.resolve_standard_types_strategies.to_bigdecimal;

import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.interfaces.istrategy.exception.StrategyException;

import java.math.BigDecimal;

/**
 * from float to bigdecimal
 */
public class FloatToBigDecimalStrategy implements IStrategy {
    @Override
    public <T> T resolve(final Object... args) throws StrategyException {
        try {
            return (T) BigDecimal.valueOf((Float) args[0]);
        } catch (Exception e) {
            throw new StrategyException(e);
        }
    }
}
