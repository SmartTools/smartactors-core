package info.smart_tools.smartactors.core.iserver;

import info.smart_tools.smartactors.core.iserver.exception.ServerExecutionException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for ScopeException
 */
public class ServerExecutionExceptionTest {
    @Test(expected = ServerExecutionException.class)
    public void checkMessageMethod()
            throws ServerExecutionException {
        String str = "test";
        ServerExecutionException exception = new ServerExecutionException(str);
        assertEquals(exception.getMessage(), str);
        throw exception;
    }

    @Test(expected = ServerExecutionException.class)
    public void checkCauseMethod()
            throws ServerExecutionException {
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        ServerExecutionException exception = new ServerExecutionException(cause);
        assertEquals(cause, exception.getCause());
        throw exception;
    }

    @Test (expected = ServerExecutionException.class)
    public void checkMessageAndCauseMethod()
            throws ServerExecutionException {
        String str = "test";
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        ServerExecutionException exception = new ServerExecutionException(str, cause);
        assertEquals(exception.getMessage(), str);
        assertEquals(exception.getCause(), cause);
        throw exception;
    }
}
