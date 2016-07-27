package info.smart_tools.smartactors.core.resolve_standard_types_strategies.to_list_strategies;

import info.smart_tools.smartactors.core.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.core.iresolve_dependency_strategy.exception.ResolveDependencyStrategyException;

import java.util.ArrayList;
import java.util.List;

/**
 * Strategy for converting from array of short primitives to list of short objects
 */
public class ShortArrayToListResolveDependencyStrategy implements IResolveDependencyStrategy {

    @Override
    public <T> T resolve(final Object... args) throws ResolveDependencyStrategyException {
        try {

            short[] shorts = (short[]) args[0];
            List<Short> result = new ArrayList<>(shorts.length);
            for (short aShort : shorts) {
                result.add(aShort);
            }

            return (T) result;
        } catch (Exception e) {
            throw new ResolveDependencyStrategyException("Can't create list from short array.", e);
        }
    }
}
