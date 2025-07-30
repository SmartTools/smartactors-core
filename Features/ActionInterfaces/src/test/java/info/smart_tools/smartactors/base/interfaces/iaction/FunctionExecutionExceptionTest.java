package info.smart_tools.smartactors.base.interfaces.iaction;

import info.smart_tools.smartactors.base.interfaces.iaction.exception.FunctionExecutionException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for FunctionExecutionException
 */
public class FunctionExecutionExceptionTest {
    @Test(expected = FunctionExecutionException.class)
    public void checkMessageMethod()
            throws FunctionExecutionException {
        String str = "test";
        FunctionExecutionException exception = new FunctionExecutionException(str);
        assertEquals(exception.getMessage(), str);
        throw exception;
    }

    @Test(expected = FunctionExecutionException.class)
    public void checkCauseMethod()
            throws FunctionExecutionException {
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        FunctionExecutionException exception = new FunctionExecutionException(cause);
        assertEquals(cause, exception.getCause());
        throw exception;
    }

    @Test (expected = FunctionExecutionException.class)
    public void checkMessageAndCauseMethod()
            throws FunctionExecutionException {
        String str = "test";
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        FunctionExecutionException exception = new FunctionExecutionException(str, cause);
        assertEquals(exception.getMessage(), str);
        assertEquals(exception.getCause(), cause);
        throw exception;
    }
}
