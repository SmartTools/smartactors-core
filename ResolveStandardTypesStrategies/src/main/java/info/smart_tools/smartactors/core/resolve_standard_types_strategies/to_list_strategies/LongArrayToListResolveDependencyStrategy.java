package info.smart_tools.smartactors.core.resolve_standard_types_strategies.to_list_strategies;

import info.smart_tools.smartactors.core.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.core.iresolve_dependency_strategy.exception.ResolveDependencyStrategyException;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Strategy for converting from array of long primitives to list of long objects
 */
public class LongArrayToListResolveDependencyStrategy implements IResolveDependencyStrategy {

    @Override
    public <T> T resolve(final Object... args) throws ResolveDependencyStrategyException {
        try {
            return (T) Arrays.stream((long[]) args[0]).boxed().collect(Collectors.toList());
        } catch (Exception e) {
            throw new ResolveDependencyStrategyException("Can't create list from long array.", e);
        }
    }
}
