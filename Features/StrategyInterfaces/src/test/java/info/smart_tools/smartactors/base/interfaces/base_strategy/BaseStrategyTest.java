package info.smart_tools.smartactors.base.interfaces.base_strategy;

import info.smart_tools.smartactors.base.interfaces.base_strategy.exception.StrategyInitializationException;
import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BaseStrategyTest {

    @Test
    public void testBaseStrategy__resolveExampleStrategy() throws Exception {
        IStrategy strategy = new ExampleStrategy();

        Integer result = strategy.resolve(5, 5);
        assertEquals(Integer.valueOf(10), result);
    }

    @Test(expected = StrategyInitializationException.class)
    public void testBaseStrategy__noResolversFound() throws Exception {
        IStrategy strategy = new StrategyWithNoResolvers();
    }

    @Test(expected = StrategyInitializationException.class)
    public void testBaseStrategy__multipleResolversFound() throws Exception {
        IStrategy strategy = new StrategyWithMultipleResolvers();
    }

    @Test(expected = StrategyInitializationException.class)
    public void testBaseStrategy__strategyIsMissingAnnotation() throws Exception {
        IStrategy strategy = new StrategyWithoutAnnotation();
    }
}

@Strategy(name = "example strategy")
class ExampleStrategy extends BaseStrategy {
    public ExampleStrategy() throws StrategyInitializationException {
        super();
    }

    @Resolver
    public Integer sum(final Integer first, final Integer second) {
        return first + second;
    }
}

@Strategy(name = "strategy with no resolvers")
class StrategyWithNoResolvers extends BaseStrategy {
    public StrategyWithNoResolvers() throws StrategyInitializationException {
        super();
    }
}

@Strategy(name = "strategy with multiple resolvers")
class StrategyWithMultipleResolvers extends BaseStrategy {
    public StrategyWithMultipleResolvers() throws StrategyInitializationException {
        super();
    }

    @Resolver
    public Integer sum(final Integer first, final Integer second) {
        return first + second;
    }

    @Resolver
    public Integer sub(final Integer first, final Integer second) {
        return first - second;
    }
}

class StrategyWithoutAnnotation extends BaseStrategy {
    public StrategyWithoutAnnotation() throws StrategyInitializationException {
        super();
    }

    @Resolver
    public Integer sum(final Integer first, final Integer second) {
        return first + second;
    }
}
