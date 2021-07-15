package info.smart_tools.smartactors.launcher.interfaces.exception.core;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link CoreLoaderException}
 */
public class CoreLoaderExceptionTest {
    @Test(expected = CoreLoaderException.class)
    public void checkMessageMethod()
            throws CoreLoaderException {
        String str = "test";
        CoreLoaderException exception = new CoreLoaderException(str);
        assertEquals(exception.getMessage(), str);
        throw exception;
    }

    @Test(expected = CoreLoaderException.class)
    public void checkCauseMethod()
            throws CoreLoaderException {
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        CoreLoaderException exception = new CoreLoaderException(cause);
        assertEquals(cause, exception.getCause());
        throw exception;
    }

    @Test (expected = CoreLoaderException.class)
    public void checkMessageAndCauseMethod()
            throws CoreLoaderException {
        String str = "test";
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        CoreLoaderException exception = new CoreLoaderException(str, cause);
        assertEquals(exception.getMessage(), str);
        assertEquals(exception.getCause(), cause);
        throw exception;
    }
}
