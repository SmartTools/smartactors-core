package info.smart_tools.smartactors.base.strategy.singleton_strategy;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.interfaces.istrategy.exception.StrategyException;

/**
 * Implementation of {@link IStrategy}
 * <pre>
 * This strategy realize singletone pattern for resolving class
 * </pre>
 */
public class SingletonStrategy implements IStrategy {

    /**
     * instance of Object
     */
    private Object classInstance;

    /**
     * Class constructor
     * Create strategy instance with initialized object
     * @param args singleton instance
     * @throws InvalidArgumentException if any errors occurred
     */
    public SingletonStrategy(final Object ... args) throws InvalidArgumentException {
        if (args.length != 1) {
            throw new InvalidArgumentException("Singleton constructor should has only one arg.");
        }
        this.classInstance = args[0];
    }

    /**
     * Represent instance of class
     * @param <T> type of object
     * @param args needed parameters for resolve dependency
     * @return instance of object
     * @throws StrategyException if any errors occurred
     */
    public <T> T resolve(final Object... args)
            throws StrategyException {
        return (T) classInstance;
    }
}
