package info.smart_tools.smartactors.core.resolve_standard_types_strategies.to_list_strategies;

import info.smart_tools.smartactors.core.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.core.iresolve_dependency_strategy.exception.ResolveDependencyStrategyException;

import java.util.ArrayList;
import java.util.List;

/**
 * Strategy for converting from array of boolean primitives to list of boolean objects
 */
public class BooleanArrayToListResolveDependencyStrategy implements IResolveDependencyStrategy {

    @Override
    public <T> T resolve(final Object... args) throws ResolveDependencyStrategyException {
        try {

            boolean[] booleans = (boolean[]) args[0];
            List<Boolean> result = new ArrayList<>(booleans.length);
            for (boolean aBoolean : booleans) {
                result.add(aBoolean);
            }

            return (T) result;
        } catch (Exception e) {
            throw new ResolveDependencyStrategyException("Can't create list from boolean array.", e);
        }
    }
}
