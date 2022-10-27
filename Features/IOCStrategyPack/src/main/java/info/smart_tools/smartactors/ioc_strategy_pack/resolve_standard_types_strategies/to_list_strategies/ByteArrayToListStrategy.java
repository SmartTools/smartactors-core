package info.smart_tools.smartactors.ioc_strategy_pack.resolve_standard_types_strategies.to_list_strategies;

import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.interfaces.istrategy.exception.StrategyException;

import java.util.ArrayList;
import java.util.List;

/**
 * Strategy for converting from array of byte primitives to list of byte objects
 */
public class ByteArrayToListStrategy implements IStrategy {

    @Override
    public <T> T resolve(final Object... args) throws StrategyException {
        try {

            byte[] bytes = (byte[]) args[0];
            List<Byte> result = new ArrayList<>(bytes.length);
            for (byte aByte : bytes) {
                result.add(aByte);
            }

            return (T) result;
        } catch (Exception e) {
            throw new StrategyException("Can't create list from byte array.", e);
        }
    }
}
