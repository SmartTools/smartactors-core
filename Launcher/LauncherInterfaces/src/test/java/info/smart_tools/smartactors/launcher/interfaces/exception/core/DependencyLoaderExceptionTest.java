package info.smart_tools.smartactors.launcher.interfaces.exception.core;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link DependencyLoaderException}
 */
public class DependencyLoaderExceptionTest {
    @Test(expected = DependencyLoaderException.class)
    public void checkMessageMethod()
            throws DependencyLoaderException {
        String str = "test";
        DependencyLoaderException exception = new DependencyLoaderException(str);
        assertEquals(exception.getMessage(), str);
        throw exception;
    }

    @Test(expected = DependencyLoaderException.class)
    public void checkCauseMethod()
            throws DependencyLoaderException {
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        DependencyLoaderException exception = new DependencyLoaderException(cause);
        assertEquals(cause, exception.getCause());
        throw exception;
    }

    @Test (expected = DependencyLoaderException.class)
    public void checkMessageAndCauseMethod()
            throws DependencyLoaderException {
        String str = "test";
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        DependencyLoaderException exception = new DependencyLoaderException(str, cause);
        assertEquals(exception.getMessage(), str);
        assertEquals(exception.getCause(), cause);
        throw exception;
    }
}
