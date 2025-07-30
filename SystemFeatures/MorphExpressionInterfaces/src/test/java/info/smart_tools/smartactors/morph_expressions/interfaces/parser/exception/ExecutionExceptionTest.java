package info.smart_tools.smartactors.morph_expressions.interfaces.parser.exception;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ExecutionExceptionTest {

    @Test(expected = ExecutionException.class)
    public void checkMessageMethod()
            throws ExecutionException {
        String str = "test";
        ExecutionException exception = new ExecutionException(str);
        assertEquals(exception.getMessage(), str);
        throw exception;
    }

    @Test (expected = ExecutionException.class)
    public void checkMessageAndCauseMethod()
            throws ExecutionException {
        String str = "test";
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        ExecutionException exception = new ExecutionException(str, cause);
        assertEquals(exception.getMessage(), str);
        assertEquals(exception.getCause(), cause);
        throw exception;
    }

}
