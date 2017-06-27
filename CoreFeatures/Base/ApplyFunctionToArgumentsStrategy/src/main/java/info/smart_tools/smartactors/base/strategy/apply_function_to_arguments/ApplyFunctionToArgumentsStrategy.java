package info.smart_tools.smartactors.base.strategy.apply_function_to_arguments;

import info.smart_tools.smartactors.base.interfaces.iaction.IFunction;
import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.base.interfaces.iresolve_dependency_strategy.exception.ResolveDependencyStrategyException;

/**
 * Implementation of {@link IResolveDependencyStrategy}
 * <pre>
 * Strategy apply function to incoming arguments
 * </pre>
 *
 * @since 1.8
 */
public class ApplyFunctionToArgumentsStrategy implements IResolveDependencyStrategy {
    /**
     * Local function for applying to arguments
     */
    private IFunction<Object[], Object> function;

    /**
     * Class constructor
     * Create instance of {@link ApplyFunctionToArgumentsStrategy}
     * @param func function
     * @throws InvalidArgumentException if any errors occurred
     */
    public ApplyFunctionToArgumentsStrategy(final IFunction<Object[], Object> func)
            throws InvalidArgumentException {
        if (null == func) {
            throw new InvalidArgumentException("Incoming argument should not be null.");
        }
        this.function = func;
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
            return (T) function.execute(args);
        } catch (Exception e) {
            throw new ResolveDependencyStrategyException("Object resolution failed: " + e.getMessage(), e);
        }
    }
}
