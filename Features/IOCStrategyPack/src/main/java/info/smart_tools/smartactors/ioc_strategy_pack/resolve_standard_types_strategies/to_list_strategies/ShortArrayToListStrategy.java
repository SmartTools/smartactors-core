package info.smart_tools.smartactors.ioc_strategy_pack.resolve_standard_types_strategies.to_list_strategies;

import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.interfaces.istrategy.exception.StrategyException;

import java.util.ArrayList;
import java.util.List;

/**
 * Strategy for converting from array of short primitives to list of short objects
 */
public class ShortArrayToListStrategy implements IStrategy {

    @Override
    public <T> T resolve(final Object... args) throws StrategyException {
        try {

            short[] shorts = (short[]) args[0];
            List<Short> result = new ArrayList<>(shorts.length);
            for (short aShort : shorts) {
                result.add(aShort);
            }

            return (T) result;
        } catch (Exception e) {
            throw new StrategyException("Can't create list from short array.", e);
        }
    }
}
