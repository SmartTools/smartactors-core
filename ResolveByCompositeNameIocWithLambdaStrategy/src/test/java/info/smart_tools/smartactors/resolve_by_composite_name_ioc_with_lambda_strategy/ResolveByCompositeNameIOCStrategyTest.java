package info.smart_tools.smartactors.resolve_by_composite_name_ioc_with_lambda_strategy;

import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.core.iresolve_dependency_strategy.exception.ResolveDependencyStrategyException;
import info.smart_tools.smartactors.core.resolve_by_composite_name_ioc_with_lambda_strategy.ResolveByCompositeNameIOCStrategy;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ResolveByCompositeNameIOCStrategyTest {

    private class Counter {
        int times;

        Counter() {
            times = 0;
        }
    }

    @Test
    public void checkStrategyCreation()
        throws InvalidArgumentException {
        IResolveDependencyStrategy strategy = new ResolveByCompositeNameIOCStrategy(
            (args)-> null
        );
        assertNotNull(strategy);
    }

    @Test
    public void checkStrategyResolutionCallInternalStrategy()
        throws Exception {
        Counter counter = new Counter();
        IResolveDependencyStrategy strategy = new ResolveByCompositeNameIOCStrategy(
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
        IResolveDependencyStrategy strategy = new ResolveByCompositeNameIOCStrategy(
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

    @Test
    public void checkStrategyResolutionConstructKey()
        throws Exception {
        Counter counter = new Counter();
        Object o = new Object();
        IResolveDependencyStrategy strategy = new ResolveByCompositeNameIOCStrategy(
            (args)-> {
                ++counter.times;
                return o;
            }
        );
        assertEquals(o, strategy.resolve("key1", "key2"));
        assertEquals(o, strategy.resolve("key1", "key3"));
        assertEquals(o, strategy.resolve("key1", "key4"));
        assertEquals(counter.times, 3);
    }

    @Test (expected = InvalidArgumentException.class)
    public void checkInvalidArgumentException()
        throws Exception {
        new ResolveByCompositeNameIOCStrategy(null);
    }

    @Test (expected = ResolveDependencyStrategyException.class)
    public void checkStrategyResolutionException()
        throws Exception {
        new ResolveByCompositeNameIOCStrategy((args)-> null).resolve("key");
    }
}

