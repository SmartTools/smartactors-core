package info.smart_tools.smartactors.core.iresolve_dependency_strategy;

import info.smart_tools.smartactors.core.iresolve_dependency_strategy.exception.ResolveDependencyStrategyException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for ResolveDependencyStrategyException
 */
public class ResolveDependencyStrategyExceptionTest {

    @Test(expected = ResolveDependencyStrategyException.class)
    public void checkMessageMethod()
            throws ResolveDependencyStrategyException {
        String str = "test";
        ResolveDependencyStrategyException exception = new ResolveDependencyStrategyException(str);
        assertEquals(exception.getMessage(), str);
        throw exception;
    }

    @Test (expected = ResolveDependencyStrategyException.class)
    public void checkCauseMethod()
            throws ResolveDependencyStrategyException {
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        ResolveDependencyStrategyException exception = new ResolveDependencyStrategyException(cause);
        assertEquals(cause, exception.getCause());
        throw exception;
    }

    @Test (expected = ResolveDependencyStrategyException.class)
    public void checkMessageAndCauseMethod()
            throws ResolveDependencyStrategyException {
        String str = "test";
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        ResolveDependencyStrategyException exception = new ResolveDependencyStrategyException(str, cause);
        assertEquals(exception.getMessage(), str);
        assertEquals(exception.getCause(), cause);
        throw exception;
    }
}
