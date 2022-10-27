package info.smart_tools.smartactors.base.interfaces.base_strategy;

import info.smart_tools.smartactors.base.interfaces.base_strategy.exception.StrategyInitializationException;
import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.interfaces.istrategy.exception.StrategyException;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

// TODO: add Javadoc
public abstract class BaseStrategy implements IStrategy {

    private final String strategyName;
    private final Method strategyResolver;

    public BaseStrategy() throws StrategyInitializationException {
        try {
            this.strategyName = this.getClass().getDeclaredAnnotation(Strategy.class).name();
        } catch (NullPointerException e) {
            throw new StrategyInitializationException(
                    String.format("Class %s extends BaseStrategy, but annotation @Strategy is missing.", this.getClass().getSimpleName())
            );
        }

        List<Method> strategyList = Arrays.stream(this.getClass().getDeclaredMethods())
                .filter(strategy -> strategy.getDeclaredAnnotation(Resolver.class) != null)
                .collect(Collectors.toList());

        if (strategyList.size() != 1) {
            throw new StrategyInitializationException("Only 1 resolver per strategy is allowed.");
        }

        this.strategyResolver = strategyList.get(0);
    }

    @Override
    public <T> T resolve(Object... args) throws StrategyException {
        try {
            return (T) strategyResolver.invoke(this, args);
        } catch (Exception e) {
            throw new StrategyException(String.format("Unable to resolve strategy '%s'.", strategyName), e);
        }
    }
}
