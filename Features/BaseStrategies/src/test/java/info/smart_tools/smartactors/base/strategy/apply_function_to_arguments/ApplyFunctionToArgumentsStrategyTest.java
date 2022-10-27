package info.smart_tools.smartactors.base.strategy.apply_function_to_arguments;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.interfaces.istrategy.exception.StrategyException;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for {@link ApplyFunctionToArgumentsStrategy}
 */
public class ApplyFunctionToArgumentsStrategyTest {
    @Test (expected = InvalidArgumentException.class)
    public void checkInvalidArgumentExceptionOnNull()
            throws InvalidArgumentException {
        new ApplyFunctionToArgumentsStrategy(null);
        fail();
    }

    @Test
    public void checkStrategyCreation() throws Exception {
        Checker checker = new Checker();
        Object value = new Object();

        IStrategy strategy = new ApplyFunctionToArgumentsStrategy(
                (args) ->{
                    checker.wasCalled = true;
                    return value;
                }
        );
        Object result = strategy.resolve();

        assertEquals(value, result);
        assertSame(value, result);
        assertTrue(checker.wasCalled);
    }

    @Test
    public void checkStrategyCreationWithArgs()
            throws Exception {
        Checker checker = new Checker();
        IStrategy strategy = new ApplyFunctionToArgumentsStrategy(
                (args) -> {
                    checker.wasCalled = true;
                    assertEquals(2, args.length);
                    assertEquals(1, args[0]);
                    assertEquals("test", args[1]);

                    return null;
                }
        );
        strategy.resolve(1, "test");
    }

    @Test (expected = StrategyException.class)
    public void checkStrategyExceptionOnWrongArgs()
            throws Exception {
        IStrategy strategy = new ApplyFunctionToArgumentsStrategy(
                (args) -> {
                    Integer a = (Integer) args[0];
                    return null;
                }
        );

        strategy.resolve();
    }
}

class Checker {
    public Boolean wasCalled = false;
}
