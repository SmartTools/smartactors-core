package info.smart_tools.smartactors.core.resolve_standard_types_strategies.to_list_strategies;

import info.smart_tools.smartactors.core.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.core.iresolve_dependency_strategy.exception.ResolveDependencyStrategyException;

import java.util.Arrays;

/**
 * Strategy for converting from array to list
 */
public class ObjectArrayToListResolveDependencyStrategy implements IResolveDependencyStrategy {

    @Override
    public <T> T resolve(final Object... args) throws ResolveDependencyStrategyException {
        try {
            return (T) Arrays.asList(args);
        } catch (Exception e) {
            throw new ResolveDependencyStrategyException("Can't create list from array.", e);
        }
    }
}
