package info.smart_tools.smartactors.launcher.interfaces.exception;

import info.smart_tools.smartactors.launcher.interfaces.exception.launcher.LauncherExecutionException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for ScopeException
 */
public class LauncherExecutionExceptionTest {
    @Test(expected = LauncherExecutionException.class)
    public void checkMessageMethod()
            throws LauncherExecutionException {
        String str = "test";
        LauncherExecutionException exception = new LauncherExecutionException(str);
        assertEquals(exception.getMessage(), str);
        throw exception;
    }

    @Test(expected = LauncherExecutionException.class)
    public void checkCauseMethod()
            throws LauncherExecutionException {
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        LauncherExecutionException exception = new LauncherExecutionException(cause);
        assertEquals(cause, exception.getCause());
        throw exception;
    }

    @Test (expected = LauncherExecutionException.class)
    public void checkMessageAndCauseMethod()
            throws LauncherExecutionException {
        String str = "test";
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        LauncherExecutionException exception = new LauncherExecutionException(str, cause);
        assertEquals(exception.getMessage(), str);
        assertEquals(exception.getCause(), cause);
        throw exception;
    }
}
