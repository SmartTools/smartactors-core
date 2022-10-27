package info.smart_tools.smartactors.ioc_strategy_pack.resolve_standard_types_strategies.to_list_strategies;

import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.interfaces.istrategy.exception.StrategyException;

import java.util.ArrayList;
import java.util.List;

/**
 * Strategy for converting from array of boolean primitives to list of boolean objects
 */
public class BooleanArrayToListStrategy implements IStrategy {

    @Override
    public <T> T resolve(final Object... args) throws StrategyException {
        try {

            boolean[] booleans = (boolean[]) args[0];
            List<Boolean> result = new ArrayList<>(booleans.length);
            for (boolean aBoolean : booleans) {
                result.add(aBoolean);
            }

            return (T) result;
        } catch (Exception e) {
            throw new StrategyException("Can't create list from boolean array.", e);
        }
    }
}
