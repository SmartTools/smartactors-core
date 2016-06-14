package info.smart_tools.smartactors.core.singleton_strategy;

import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iresolve_dependency_strategy.IResolveDependencyStrategy;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

/**
 * Tests for {@link SingletonStrategy}
 */
public class SingletonStrategyTest {

    @Test (expected = InvalidArgumentException.class)
    public void checkSingletonStrategyCreation()
            throws Exception {
        IResolveDependencyStrategy strategy = new SingletonStrategy();
        assertNotNull(strategy);
        fail();
    }

    @Test
    public void checkStrategyResolution()
            throws Exception {
        Object value = new String("a");
        Object other = new String("a");
        IResolveDependencyStrategy strategy = new SingletonStrategy(value);
        Object result = strategy.resolve();

        assertNotSame(value, other);
        assertEquals(value, result);
        assertSame(value, result);
        assertEquals(other, result);
        assertNotSame(other, result);
    }

}
