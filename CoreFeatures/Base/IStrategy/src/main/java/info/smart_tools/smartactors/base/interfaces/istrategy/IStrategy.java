package info.smart_tools.smartactors.base.interfaces.istrategy;

import info.smart_tools.smartactors.base.interfaces.istrategy.exception.StrategyException;

/**
 * IStrategy
 */
public interface IStrategy {

    /**
     * Resolve dependency by realized strategy
     * @param args array of needed parameters for resolve dependency
     * @param <T> type of object
     * @return instance of type T object
     * @throws StrategyException if any errors occurred
     */
    <T> T resolve(Object ... args)
            throws StrategyException;
}
