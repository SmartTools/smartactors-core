package info.smart_tools.smartactors.launcher.interfaces.exception;

import info.smart_tools.smartactors.launcher.interfaces.exception.launcher.LauncherInitializeException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for ScopeException
 */
public class LauncherInitializeExceptionTest {
    @Test(expected = LauncherInitializeException.class)
    public void checkMessageMethod()
            throws LauncherInitializeException {
        String str = "test";
        LauncherInitializeException exception = new LauncherInitializeException(str);
        assertEquals(exception.getMessage(), str);
        throw exception;
    }

    @Test(expected = LauncherInitializeException.class)
    public void checkCauseMethod()
            throws LauncherInitializeException {
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        LauncherInitializeException exception = new LauncherInitializeException(cause);
        assertEquals(cause, exception.getCause());
        throw exception;
    }

    @Test (expected = LauncherInitializeException.class)
    public void checkMessageAndCauseMethod()
            throws LauncherInitializeException {
        String str = "test";
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        LauncherInitializeException exception = new LauncherInitializeException(str, cause);
        assertEquals(exception.getMessage(), str);
        assertEquals(exception.getCause(), cause);
        throw exception;
    }
}
