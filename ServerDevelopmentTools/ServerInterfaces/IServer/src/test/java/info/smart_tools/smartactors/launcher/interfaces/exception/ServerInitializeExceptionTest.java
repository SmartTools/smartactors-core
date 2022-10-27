package info.smart_tools.smartactors.launcher.interfaces.exception;

import info.smart_tools.smartactors.launcher.interfaces.exception.exception.ServerInitializeException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for ScopeException
 */
public class ServerInitializeExceptionTest {
    @Test(expected = ServerInitializeException.class)
    public void checkMessageMethod()
            throws ServerInitializeException {
        String str = "test";
        ServerInitializeException exception = new ServerInitializeException(str);
        assertEquals(exception.getMessage(), str);
        throw exception;
    }

    @Test(expected = ServerInitializeException.class)
    public void checkCauseMethod()
            throws ServerInitializeException {
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        ServerInitializeException exception = new ServerInitializeException(cause);
        assertEquals(cause, exception.getCause());
        throw exception;
    }

    @Test (expected = ServerInitializeException.class)
    public void checkMessageAndCauseMethod()
            throws ServerInitializeException {
        String str = "test";
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        ServerInitializeException exception = new ServerInitializeException(str, cause);
        assertEquals(exception.getMessage(), str);
        assertEquals(exception.getCause(), cause);
        throw exception;
    }
}
