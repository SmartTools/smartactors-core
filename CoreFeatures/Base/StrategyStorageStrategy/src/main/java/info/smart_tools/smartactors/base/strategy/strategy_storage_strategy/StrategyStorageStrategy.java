package info.smart_tools.smartactors.base.strategy.strategy_storage_strategy;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.istrategy_registration.IStrategyRegistration;
import info.smart_tools.smartactors.base.interfaces.istrategy_registration.exception.StrategyRegistrationException;
import info.smart_tools.smartactors.base.interfaces.iaction.IFunctionTwoArgs;
import info.smart_tools.smartactors.base.interfaces.iaction.IFunction;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.FunctionExecutionException;
import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.interfaces.istrategy.exception.StrategyException;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Strategy to storage some specific strategies united by a common purpose
 */
public class StrategyStorageStrategy implements IStrategy, IStrategyRegistration {

    /**
     * Strategy storage
     */
    private ConcurrentMap<Object, IStrategy> strategyStorage;
    private IFunction argToKeyFunction;
    private IFunctionTwoArgs findValueByArgumentFunction;


    /**
     * @param argToKeyFunction the function that transforms argument to a map key
     * @param findValueByArgumentFunction the function that finds value by given argument in the given map
     */
    public StrategyStorageStrategy(final IFunction argToKeyFunction, final IFunctionTwoArgs findValueByArgumentFunction) {
        this.strategyStorage = new ConcurrentHashMap<>();
        this.argToKeyFunction = argToKeyFunction;
        this.findValueByArgumentFunction = findValueByArgumentFunction;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T resolve(final Object... args) throws StrategyException {
        try {
            IStrategy strategy = (IStrategy) this.findValueByArgumentFunction.execute(this.strategyStorage, args[0]);

            return null == strategy ? null : strategy.resolve(args);
        } catch (Exception e) {
            throw new StrategyException("Object resolution failed.", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void register(final Object arg, final IStrategy value)
            throws StrategyRegistrationException {
        try {
            this.strategyStorage.put(this.argToKeyFunction.execute(arg), value);
        } catch (InvalidArgumentException | FunctionExecutionException e) {
            throw new StrategyRegistrationException(e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public IStrategy unregister(final Object arg)
            throws StrategyRegistrationException {
        try {
            return this.strategyStorage.remove(this.argToKeyFunction.execute(arg));
        } catch (FunctionExecutionException | InvalidArgumentException e) {
            throw new StrategyRegistrationException(e);
        }
    }
}
