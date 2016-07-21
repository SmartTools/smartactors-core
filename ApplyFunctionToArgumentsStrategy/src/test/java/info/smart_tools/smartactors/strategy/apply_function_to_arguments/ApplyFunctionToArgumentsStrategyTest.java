package info.smart_tools.smartactors.strategy.apply_function_to_arguments;

import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.core.iresolve_dependency_strategy.exception.ResolveDependencyStrategyException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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

        IResolveDependencyStrategy strategy = new ApplyFunctionToArgumentsStrategy(
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
        IResolveDependencyStrategy strategy = new ApplyFunctionToArgumentsStrategy(
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

    @Test (expected = ResolveDependencyStrategyException.class)
    public void checkResolveDependencyStrategyExceptionOnWrongArgs()
            throws Exception {
        IResolveDependencyStrategy strategy = new ApplyFunctionToArgumentsStrategy(
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
