package info.smart_tools.smartactors.launcher.interfaces.exception.ibootstrap;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link BootstrapInitException}
 */
public class BootstrapInitExceptionTest {
    @Test(expected = BootstrapInitException.class)
    public void checkMessageMethod()
            throws BootstrapInitException {
        String str = "test";
        BootstrapInitException exception = new BootstrapInitException(str);
        assertEquals(exception.getMessage(), str);
        throw exception;
    }

    @Test(expected = BootstrapInitException.class)
    public void checkCauseMethod()
            throws BootstrapInitException {
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        BootstrapInitException exception = new BootstrapInitException(cause);
        assertEquals(cause, exception.getCause());
        throw exception;
    }

    @Test (expected = BootstrapInitException.class)
    public void checkMessageAndCauseMethod()
            throws BootstrapInitException {
        String str = "test";
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        BootstrapInitException exception = new BootstrapInitException(str, cause);
        assertEquals(exception.getMessage(), str);
        assertEquals(exception.getCause(), cause);
        throw exception;
    }
}
