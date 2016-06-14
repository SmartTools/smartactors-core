package info.smart_tools.smartactors.core.resolve_by_name_ioc_with_lambda_strategy;

import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.core.iresolve_dependency_strategy.exception.ResolveDependencyStrategyException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests for ResolveByNameIocStrategy
 */
public class ResolveByNameIocStrategyTest {

    @Test
    public void checkStrategyCreation()
            throws InvalidArgumentException {
        IResolveDependencyStrategy strategy = new ResolveByNameIocStrategy(
                (args)-> {
                    return null;
                }
        );
        assertNotNull(strategy);
    }

    @Test
    public void checkStrategyResolutionCallInternalStrategy()
            throws Exception {
        Counter counter = new Counter();
        IResolveDependencyStrategy strategy = new ResolveByNameIocStrategy(
                (args)-> {
                    ++counter.times;
                    return "";
                }
        );
        strategy.resolve("key");
        assertEquals(counter.times, 1);
    }

    @Test
    public void checkStrategyResolutionOnceCallInternalStrategy()
            throws Exception {
        Counter counter = new Counter();
        Object o = new Object();
        IResolveDependencyStrategy strategy = new ResolveByNameIocStrategy(
                (args)-> {
                    ++counter.times;
                    return o;
                }
        );
        assertEquals(o, strategy.resolve("key"));
        assertEquals(o, strategy.resolve("key"));
        assertEquals(o, strategy.resolve("key"));
        assertEquals(counter.times, 1);
    }

    @Test (expected = InvalidArgumentException.class)
    public void checkInvalidArgumentException()
            throws Exception {
        new ResolveByNameIocStrategy(null);
    }

    @Test (expected = ResolveDependencyStrategyException.class)
    public void checkStrategyResolutionException()
            throws Exception {
        new ResolveByNameIocStrategy((args)-> null).resolve("key");
    }
}

class Counter {
    public int times;

    public Counter() {
        times = 0;
    }
}