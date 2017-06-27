package info.smart_tools.smartactors.ioc.resolve_by_name_ioc_with_lambda_strategy;

import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.base.interfaces.iresolve_dependency_strategy.exception.ResolveDependencyStrategyException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Implementation of {@link IResolveDependencyStrategy}
 * <pre>
 * Strategy allows to storage instances of {@link IKey}
 * </pre>
 *
 * @since 1.8
 */
public class ResolveByNameIocStrategy implements IResolveDependencyStrategy {

    /**
     * Local {@link IKey} instance storage
     */
    private Map<String, Object> storage = new ConcurrentHashMap<>();

    /**
     * Function to create object for storing
     */
    private Function<Object[], Object> strategy;

    /**
     * Strategy constructor
     * @param strategy function to create object for storing
     * @throws InvalidArgumentException if any errors occurred
     */
    public ResolveByNameIocStrategy(final Function<Object[], Object> strategy)
            throws InvalidArgumentException {
        if (null == strategy) {
            throw new InvalidArgumentException("Strategy should not be null");
        }
        this.strategy = strategy;
    }

    /**
     * Return stored instance of {@link IKey} if exists
     * otherwise create new instance of {@link IKey}, store to the local storage and return
     * @param <T> type of object
     * @param args needed parameters for resolve dependency
     * @return instance of object
     * @throws ResolveDependencyStrategyException if any errors occurred
     */
    @Override
    public <T> T resolve(final Object... args)
            throws ResolveDependencyStrategyException {
        try {
            Object result = storage.get((String) args[0]);
            if (null == result) {
                result = strategy.apply(args);
                storage.put((String) args[0], result);
            }
            return (T) result;
        } catch (Exception e) {
            throw new ResolveDependencyStrategyException("Object resolution failed: " + e.getMessage(), e);
        }
    }
}
