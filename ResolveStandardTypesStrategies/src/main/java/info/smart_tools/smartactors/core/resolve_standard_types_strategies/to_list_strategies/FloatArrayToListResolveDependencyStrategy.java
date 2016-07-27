package info.smart_tools.smartactors.core.resolve_standard_types_strategies.to_list_strategies;

import info.smart_tools.smartactors.core.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.core.iresolve_dependency_strategy.exception.ResolveDependencyStrategyException;

import java.util.ArrayList;
import java.util.List;

/**
 * Strategy for converting from array of float primitives to list of float objects
 */
public class FloatArrayToListResolveDependencyStrategy implements IResolveDependencyStrategy {

    @Override
    public <T> T resolve(final Object... args) throws ResolveDependencyStrategyException {
        try {

            float[] floats = (float[]) args[0];
            List<Float> result = new ArrayList<>(floats.length);
            for (float aFloat : floats) {
                result.add(aFloat);
            }

            return (T) result;
        } catch (Exception e) {
            throw new ResolveDependencyStrategyException("Can't create list from float array.", e);
        }
    }
}
