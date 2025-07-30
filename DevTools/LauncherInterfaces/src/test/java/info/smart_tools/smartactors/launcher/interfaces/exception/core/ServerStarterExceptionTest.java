package info.smart_tools.smartactors.launcher.interfaces.exception.core;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link ServerStarterException}
 */
public class ServerStarterExceptionTest {
    @Test(expected = ServerStarterException.class)
    public void checkMessageMethod()
            throws ServerStarterException {
        String str = "test";
        ServerStarterException exception = new ServerStarterException(str);
        assertEquals(exception.getMessage(), str);
        throw exception;
    }

    @Test(expected = ServerStarterException.class)
    public void checkCauseMethod()
            throws ServerStarterException {
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        ServerStarterException exception = new ServerStarterException(cause);
        assertEquals(cause, exception.getCause());
        throw exception;
    }

    @Test (expected = ServerStarterException.class)
    public void checkMessageAndCauseMethod()
            throws ServerStarterException {
        String str = "test";
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        ServerStarterException exception = new ServerStarterException(str, cause);
        assertEquals(exception.getMessage(), str);
        assertEquals(exception.getCause(), cause);
        throw exception;
    }
}
