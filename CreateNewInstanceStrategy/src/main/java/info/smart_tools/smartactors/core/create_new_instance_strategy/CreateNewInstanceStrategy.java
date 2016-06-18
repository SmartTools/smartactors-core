package info.smart_tools.smartactors.core.create_new_instance_strategy;

import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.core.iresolve_dependency_strategy.exception.ResolveDependencyStrategyException;

import java.util.function.Function;

/**
 * Implementation of {@link IResolveDependencyStrategy}
 * <pre>
 * Strategy allows to create new instances of specified classes
 * </pre>
 *
 * @since 1.8
 */
public class CreateNewInstanceStrategy implements IResolveDependencyStrategy {

    /**
     * Local function for creation new instances of classes
     */
    private Function<Object[], Object> creationFunction;

    /**
     * Class constructor
     * Create instance of {@link CreateNewInstanceStrategy}
     * @param func function to create new object instance
     * @throws InvalidArgumentException if any errors occurred
     */
    public CreateNewInstanceStrategy(final Function<Object[], Object> func)
            throws InvalidArgumentException {
        if (null == func) {
            throw new InvalidArgumentException("Incoming argument should not be null.");
        }
        this.creationFunction = func;
    }

    /**
     * Represent new instance of class by given param
     * @param <T> type of object
     * @param args needed parameters for resolve dependency
     * @return instance of object
     * @throws ResolveDependencyStrategyException if any errors occurred
     */
    public <T> T resolve(final Object ... args)
            throws ResolveDependencyStrategyException {
        try {
            return (T) creationFunction.apply(args);
        } catch (Exception e) {
            throw new ResolveDependencyStrategyException("Object resolution failed.", e);
        }
    }
}
