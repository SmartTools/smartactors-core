package info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap;

import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.exception.ProcessExecutionException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for ProcessExecutionException
 */
public class ProcessExecutionExceptionTest {

    @Test (expected = ProcessExecutionException.class)
    public void checkMessageMethod()
            throws ProcessExecutionException {
        String str = "test";
        ProcessExecutionException exception = new ProcessExecutionException(str);
        assertEquals(exception.getMessage(), str);
        throw exception;
    }

    @Test (expected = ProcessExecutionException.class)
    public void checkCauseMethod()
            throws ProcessExecutionException {
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        ProcessExecutionException exception = new ProcessExecutionException(cause);
        assertEquals(cause, exception.getCause());
        throw exception;
    }

    @Test (expected = ProcessExecutionException.class)
    public void checkMessageAndCauseMethod()
            throws ProcessExecutionException {
        String str = "test";
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        ProcessExecutionException exception = new ProcessExecutionException(str, cause);
        assertEquals(exception.getMessage(), str);
        assertEquals(exception.getCause(), cause);
        throw exception;
    }
}
