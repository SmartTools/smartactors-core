package info.smart_tools.smartactors.core.resolve_standard_types_strategies.to_list_strategies;

import info.smart_tools.smartactors.core.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.core.iresolve_dependency_strategy.exception.ResolveDependencyStrategyException;

import java.util.ArrayList;
import java.util.List;

/**
 * Strategy for converting from array of byte primitives to list of byte objects
 */
public class ByteArrayToListResolveDependencyStrategy implements IResolveDependencyStrategy {

    @Override
    public <T> T resolve(final Object... args) throws ResolveDependencyStrategyException {
        try {

            byte[] bytes = (byte[]) args[0];
            List<Byte> result = new ArrayList<>(bytes.length);
            for (byte aByte : bytes) {
                result.add(aByte);
            }

            return (T) result;
        } catch (Exception e) {
            throw new ResolveDependencyStrategyException("Can't create list from byte array.", e);
        }
    }
}
