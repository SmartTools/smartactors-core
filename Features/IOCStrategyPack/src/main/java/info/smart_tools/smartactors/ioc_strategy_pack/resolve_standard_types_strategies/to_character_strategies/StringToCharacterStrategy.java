package info.smart_tools.smartactors.ioc_strategy_pack.resolve_standard_types_strategies.to_character_strategies;

import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.interfaces.istrategy.exception.StrategyException;

/**
 * Converts from String to Character
 */
public class StringToCharacterStrategy implements IStrategy {

    @Override
    public <T> T resolve(final Object... args) throws StrategyException {
        try {
            return (T) new Character(String.valueOf(args[0]).charAt(0));
        } catch (Exception e) {
            throw new StrategyException(e);
        }
    }
}
