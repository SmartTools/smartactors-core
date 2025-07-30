package info.smart_tools.smartactors.base.strategy.singleton_strategy;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for {@link SingletonStrategy}
 */
public class SingletonStrategyTest {

    @Test (expected = InvalidArgumentException.class)
    public void checkSingletonStrategyCreation()
            throws Exception {
        IStrategy strategy = new SingletonStrategy();
        assertNotNull(strategy);
        fail();
    }

    @Test
    public void checkStrategyResolution()
            throws Exception {
        Object value = new String("a");
        Object other = new String("a");
        IStrategy strategy = new SingletonStrategy(value);
        Object result = strategy.resolve();

        assertNotSame(value, other);
        assertEquals(value, result);
        assertSame(value, result);
        assertEquals(other, result);
        assertNotSame(other, result);
    }

}
