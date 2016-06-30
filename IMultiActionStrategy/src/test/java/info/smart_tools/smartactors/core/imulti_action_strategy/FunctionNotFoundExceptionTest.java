package info.smart_tools.smartactors.core.imulti_action_strategy;

import info.smart_tools.smartactors.core.imulti_action_strategy.exception.FunctionNotFoundException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for FunctionNotFoundException
 */
public class FunctionNotFoundExceptionTest {

    @Test (expected = FunctionNotFoundException.class)
    public void checkMessageMethod()
            throws FunctionNotFoundException {
        String str = "test";
        FunctionNotFoundException exception = new FunctionNotFoundException(str);
        assertEquals(exception.getMessage(), str);
        throw exception;
    }

    @Test (expected = FunctionNotFoundException.class)
    public void checkCauseMethod()
            throws FunctionNotFoundException {
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        FunctionNotFoundException exception = new FunctionNotFoundException(cause);
        assertEquals(cause, exception.getCause());
        throw exception;
    }

    @Test (expected = FunctionNotFoundException.class)
    public void checkMessageAndCauseMethod()
            throws FunctionNotFoundException {
        String str = "test";
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        FunctionNotFoundException exception = new FunctionNotFoundException(str, cause);
        assertEquals(exception.getMessage(), str);
        assertEquals(exception.getCause(), cause);
        throw exception;
    }
}
