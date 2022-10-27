package info.smart_tools.smartactors.ioc_strategy_pack.resolve_standard_types_strategies.to_boolean_strategies;

import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.interfaces.istrategy.exception.StrategyException;

/**
 * Used to convert from Boolean to boolean.
 */
public class BooleanToPrimitiveStrategy implements IStrategy {

    @Override
    public <T> T resolve(final Object... args) throws StrategyException {

        Boolean value = args[0] == null ? false : (Boolean) args[0];
        return (T) value;
    }
}
