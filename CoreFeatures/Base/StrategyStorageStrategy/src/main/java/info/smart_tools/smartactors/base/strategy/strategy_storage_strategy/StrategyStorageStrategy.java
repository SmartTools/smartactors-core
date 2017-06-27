package info.smart_tools.smartactors.base.strategy.strategy_storage_strategy;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.i_addition_dependency_strategy.IAdditionDependencyStrategy;
import info.smart_tools.smartactors.base.interfaces.i_addition_dependency_strategy.exception.AdditionDependencyStrategyException;
import info.smart_tools.smartactors.base.interfaces.iaction.IBiFunction;
import info.smart_tools.smartactors.base.interfaces.iaction.IFunction;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.FunctionExecutionException;
import info.smart_tools.smartactors.base.interfaces.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.base.interfaces.iresolve_dependency_strategy.exception.ResolveDependencyStrategyException;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Strategy to storage some specific strategies united by a common purpose
 */
public class StrategyStorageStrategy implements IResolveDependencyStrategy, IAdditionDependencyStrategy {

    /**
     * Strategy storage
     */
    private ConcurrentMap<Object, IResolveDependencyStrategy> strategyStorage;
    private IFunction argToKeyFunction;
    private IBiFunction findValueByArgumentFunction;


    /**
     * @param argToKeyFunction the function that transforms argument to a map key
     * @param findValueByArgumentFunction the function that finds value by given argument in the given map
     */
    public StrategyStorageStrategy(final IFunction argToKeyFunction, final IBiFunction findValueByArgumentFunction) {
        this.strategyStorage = new ConcurrentHashMap<>();
        this.argToKeyFunction = argToKeyFunction;
        this.findValueByArgumentFunction = findValueByArgumentFunction;
    }

    @Override
    public <T> T resolve(Object... args) throws ResolveDependencyStrategyException {
        try {
            IResolveDependencyStrategy strategy = (IResolveDependencyStrategy) this.findValueByArgumentFunction.execute(this.strategyStorage, args[0]);

            return null == strategy ? null : strategy.resolve(args);
        } catch (Exception e) {
            throw new ResolveDependencyStrategyException("Object resolution failed: " + e.getMessage(), e);
        }
    }

    @Override
    public void register(Object arg, IResolveDependencyStrategy value)
            throws AdditionDependencyStrategyException {
        try {
            this.strategyStorage.put(this.argToKeyFunction.execute(arg), value);
        } catch (InvalidArgumentException | FunctionExecutionException e) {
            throw new AdditionDependencyStrategyException(e);
        }
    }

    @Override
    public void remove(Object arg)
            throws AdditionDependencyStrategyException {
        try {
            this.strategyStorage.remove(this.argToKeyFunction.execute(arg));
        } catch (FunctionExecutionException | InvalidArgumentException e) {
            throw new AdditionDependencyStrategyException(e);
        }
    }
}
