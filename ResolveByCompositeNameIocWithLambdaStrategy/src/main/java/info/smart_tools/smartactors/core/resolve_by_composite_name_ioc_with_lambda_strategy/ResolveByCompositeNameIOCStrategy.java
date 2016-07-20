package info.smart_tools.smartactors.core.resolve_by_composite_name_ioc_with_lambda_strategy;

import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.core.iresolve_dependency_strategy.exception.ResolveDependencyStrategyException;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * Implementation of {@link IResolveDependencyStrategy}
 * <pre>
 * Strategy looks for needed object inside cache by key, which it constructs
 * from string representations of resolve arguments. If object wouldn't found,
 * then creation function would be applied.
 * </pre>
 *
 * @since 1.8
 */
public class ResolveByCompositeNameIOCStrategy implements IResolveDependencyStrategy {

    /**
     * Storage of created instances
     */
    private ConcurrentMap<String, Object> cache;
    /**
     * Local function for creation new instances of classes
     */
    private Function<Object[], Object> creationFunction;

    /**
     * Constructor.
     * @param func function to create new object instance
     * @throws InvalidArgumentException if any errors occurred
     */
    public ResolveByCompositeNameIOCStrategy(final Function<Object[], Object> func) throws InvalidArgumentException {
        if (null == func) {
            throw new InvalidArgumentException("Incoming argument should not be null.");
        }
        this.cache = new ConcurrentHashMap<>();
        this.creationFunction = func;

    }

    /**
     * Resolves instance of current type.
     * @param args array of needed parameters for resolve dependency
     * @param <T> type of object
     * @return object instance
     * @throws ResolveDependencyStrategyException
     */
    @Override
    public <T> T resolve(final Object... args) throws ResolveDependencyStrategyException {

        try {
            StringBuilder builder = new StringBuilder();
            for (Object arg : args) {
                builder = builder.append(String.valueOf(arg));
            }
            String key = builder.toString();
            Object result = cache.get(key);
            if (result == null) {
                result = creationFunction.apply(args);
                cache.putIfAbsent(key, result);
            }
            return (T) result;
        } catch (Exception e) {
            throw new ResolveDependencyStrategyException("Object resolution failed.", e);
        }
    }
}
