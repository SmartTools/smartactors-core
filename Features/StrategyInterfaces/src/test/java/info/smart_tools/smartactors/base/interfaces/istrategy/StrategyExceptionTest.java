package info.smart_tools.smartactors.base.interfaces.istrategy;

import info.smart_tools.smartactors.base.interfaces.istrategy.exception.StrategyException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for StrategyException
 */
public class StrategyExceptionTest {

    @Test(expected = StrategyException.class)
    public void checkMessageMethod()
            throws StrategyException {
        String str = "test";
        StrategyException exception = new StrategyException(str);
        assertEquals(exception.getMessage(), str);
        throw exception;
    }

    @Test (expected = StrategyException.class)
    public void checkCauseMethod()
            throws StrategyException {
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        StrategyException exception = new StrategyException(cause);
        assertEquals(cause, exception.getCause());
        throw exception;
    }

    @Test (expected = StrategyException.class)
    public void checkMessageAndCauseMethod()
            throws StrategyException {
        String str = "test";
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        StrategyException exception = new StrategyException(str, cause);
        assertEquals(exception.getMessage(), str);
        assertEquals(exception.getCause(), cause);
        throw exception;
    }
}
