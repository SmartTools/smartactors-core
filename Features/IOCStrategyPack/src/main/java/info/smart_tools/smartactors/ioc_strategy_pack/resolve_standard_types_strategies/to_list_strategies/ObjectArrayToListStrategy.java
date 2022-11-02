package info.smart_tools.smartactors.ioc_strategy_pack.resolve_standard_types_strategies.to_list_strategies;

import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.interfaces.istrategy.exception.StrategyException;

import java.util.Arrays;

/**
 * Strategy for converting from array to list
 */
public class ObjectArrayToListStrategy implements IStrategy {

    @Override
    public <T> T resolve(final Object... args) throws StrategyException {
        try {
            return (T) Arrays.asList(args);
        } catch (Exception e) {
            throw new StrategyException("Can't create list from array.", e);
        }
    }
}
