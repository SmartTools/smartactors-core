package info.smart_tools.smartactors.core.resolve_standard_types_strategies.to_list_strategies;

import info.smart_tools.smartactors.core.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.core.iresolve_dependency_strategy.exception.ResolveDependencyStrategyException;

import java.util.ArrayList;
import java.util.List;

/**
 * Strategy for converting from array of char primitives to list of character objects
 */
public class CharArrayToListResolveDependencyStrategy implements IResolveDependencyStrategy {

    @Override
    public <T> T resolve(final Object... args) throws ResolveDependencyStrategyException {
        try {

            char[] chars = (char[]) args[0];
            List<Character> result = new ArrayList<>(chars.length);
            for (char aChar : chars) {
                result.add(aChar);
            }

            return (T) result;
        } catch (Exception e) {
            throw new ResolveDependencyStrategyException("Can't create list from char array.", e);
        }
    }
}
