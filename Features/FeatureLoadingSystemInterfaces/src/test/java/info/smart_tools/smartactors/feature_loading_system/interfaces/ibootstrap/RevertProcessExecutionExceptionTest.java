package info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap;

import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.exception.RevertProcessExecutionException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for RevertProcessExecutionException
 */
public class RevertProcessExecutionExceptionTest {

    @Test (expected = RevertProcessExecutionException.class)
    public void checkMessageMethod()
            throws RevertProcessExecutionException {
        String str = "test";
        RevertProcessExecutionException exception = new RevertProcessExecutionException(str);
        assertEquals(exception.getMessage(), str);
        throw exception;
    }

    @Test (expected = RevertProcessExecutionException.class)
    public void checkCauseMethod()
            throws RevertProcessExecutionException {
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        RevertProcessExecutionException exception = new RevertProcessExecutionException(cause);
        assertEquals(cause, exception.getCause());
        throw exception;
    }

    @Test (expected = RevertProcessExecutionException.class)
    public void checkMessageAndCauseMethod()
            throws RevertProcessExecutionException {
        String str = "test";
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        RevertProcessExecutionException exception = new RevertProcessExecutionException(str, cause);
        assertEquals(exception.getMessage(), str);
        assertEquals(exception.getCause(), cause);
        throw exception;
    }
}
