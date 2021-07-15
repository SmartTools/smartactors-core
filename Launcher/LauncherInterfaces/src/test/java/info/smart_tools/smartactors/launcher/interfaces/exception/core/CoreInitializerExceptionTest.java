package info.smart_tools.smartactors.launcher.interfaces.exception.core;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link CoreInitializerException}
 */
public class CoreInitializerExceptionTest {
    @Test(expected = CoreInitializerException.class)
    public void checkMessageMethod()
            throws CoreInitializerException {
        String str = "test";
        CoreInitializerException exception = new CoreInitializerException(str);
        assertEquals(exception.getMessage(), str);
        throw exception;
    }

    @Test(expected = CoreInitializerException.class)
    public void checkCauseMethod()
            throws CoreInitializerException {
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        CoreInitializerException exception = new CoreInitializerException(cause);
        assertEquals(cause, exception.getCause());
        throw exception;
    }

    @Test (expected = CoreInitializerException.class)
    public void checkMessageAndCauseMethod()
            throws CoreInitializerException {
        String str = "test";
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        CoreInitializerException exception = new CoreInitializerException(str, cause);
        assertEquals(exception.getMessage(), str);
        assertEquals(exception.getCause(), cause);
        throw exception;
    }
}
