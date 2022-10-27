package info.smart_tools.smartactors.base.interfaces.iaction;

import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for ActionExecutionException
 */
public class ActionExecutionExceptionTest {
    @Test(expected = ActionExecutionException.class)
    public void checkMessageMethod()
            throws ActionExecutionException {
        String str = "test";
        ActionExecutionException exception = new ActionExecutionException(str);
        assertEquals(exception.getMessage(), str);
        throw exception;
    }

    @Test(expected = ActionExecutionException.class)
    public void checkCauseMethod()
            throws ActionExecutionException {
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        ActionExecutionException exception = new ActionExecutionException(cause);
        assertEquals(cause, exception.getCause());
        throw exception;
    }

    @Test (expected = ActionExecutionException.class)
    public void checkMessageAndCauseMethod()
            throws ActionExecutionException {
        String str = "test";
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        ActionExecutionException exception = new ActionExecutionException(str, cause);
        assertEquals(exception.getMessage(), str);
        assertEquals(exception.getCause(), cause);
        throw exception;
    }
}
