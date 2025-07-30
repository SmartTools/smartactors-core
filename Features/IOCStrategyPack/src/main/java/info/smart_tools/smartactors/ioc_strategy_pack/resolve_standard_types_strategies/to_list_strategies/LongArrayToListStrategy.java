package info.smart_tools.smartactors.ioc_strategy_pack.resolve_standard_types_strategies.to_list_strategies;

import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.interfaces.istrategy.exception.StrategyException;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Strategy for converting from array of long primitives to list of long objects
 */
public class LongArrayToListStrategy implements IStrategy {

    @Override
    public <T> T resolve(final Object... args) throws StrategyException {
        try {
            return (T) Arrays.stream((long[]) args[0]).boxed().collect(Collectors.toList());
        } catch (Exception e) {
            throw new StrategyException("Can't create list from long array.", e);
        }
    }
}
