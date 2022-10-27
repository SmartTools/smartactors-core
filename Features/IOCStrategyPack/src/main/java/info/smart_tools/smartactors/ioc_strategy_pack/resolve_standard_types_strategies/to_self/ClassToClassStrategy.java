package info.smart_tools.smartactors.ioc_strategy_pack.resolve_standard_types_strategies.to_self;

import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.interfaces.istrategy.exception.StrategyException;

/**
 * Strategy for converting from class to itself
 */
public class ClassToClassStrategy implements IStrategy {

    @Override
    public <T> T resolve(final Object... args) throws StrategyException {
            return (T) args[0];
    }
}
