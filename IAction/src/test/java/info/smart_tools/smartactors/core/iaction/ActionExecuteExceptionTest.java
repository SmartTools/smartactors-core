package info.smart_tools.smartactors.core.iaction;

import info.smart_tools.smartactors.core.iaction.exception.ActionExecuteException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for ActionExecuteException
 */
public class ActionExecuteExceptionTest {
    @Test(expected = ActionExecuteException.class)
    public void checkMessageMethod()
            throws ActionExecuteException {
        String str = "test";
        ActionExecuteException exception = new ActionExecuteException(str);
        assertEquals(exception.getMessage(), str);
        throw exception;
    }

    @Test(expected = ActionExecuteException.class)
    public void checkCauseMethod()
            throws ActionExecuteException {
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        ActionExecuteException exception = new ActionExecuteException(cause);
        assertEquals(cause, exception.getCause());
        throw exception;
    }

    @Test (expected = ActionExecuteException.class)
    public void checkMessageAndCauseMethod()
            throws ActionExecuteException {
        String str = "test";
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        ActionExecuteException exception = new ActionExecuteException(str, cause);
        assertEquals(exception.getMessage(), str);
        assertEquals(exception.getCause(), cause);
        throw exception;
    }
}
