package info.smart_tools.smartactors.ioc.istrategy_container;

import info.smart_tools.smartactors.ioc.istrategy_container.exception.StrategyContainerException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for StrategyContainerException
 */
public class StrategyContainerExceptionTest {

    @Test(expected = StrategyContainerException.class)
    public void checkMessageMethod()
            throws StrategyContainerException {
        String str = "test";
        StrategyContainerException exception = new StrategyContainerException(str);
        assertEquals(exception.getMessage(), str);
        throw exception;
    }

    @Test (expected = StrategyContainerException.class)
    public void checkCauseMethod()
            throws StrategyContainerException {
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        StrategyContainerException exception = new StrategyContainerException(cause);
        assertEquals(cause, exception.getCause());
        throw exception;
    }

    @Test (expected = StrategyContainerException.class)
    public void checkMessageAndCauseMethod()
            throws StrategyContainerException {
        String str = "test";
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        StrategyContainerException exception = new StrategyContainerException(str, cause);
        assertEquals(exception.getMessage(), str);
        assertEquals(exception.getCause(), cause);
        throw exception;
    }
}
