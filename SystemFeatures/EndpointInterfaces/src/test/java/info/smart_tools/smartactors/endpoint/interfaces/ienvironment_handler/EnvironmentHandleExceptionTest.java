package info.smart_tools.smartactors.endpoint.interfaces.ienvironment_handler;

import info.smart_tools.smartactors.endpoint.interfaces.ienvironment_handler.exception.EnvironmentHandleException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link EnvironmentHandleException}
 */
public class EnvironmentHandleExceptionTest {

    @Test(expected = EnvironmentHandleException.class)
    public void checkMessageMethod()
            throws EnvironmentHandleException {
        String str = "test";
        EnvironmentHandleException exception = new EnvironmentHandleException(str);
        assertEquals(exception.getMessage(), str);
        throw exception;
    }

    @Test (expected = EnvironmentHandleException.class)
    public void checkCauseMethod()
            throws EnvironmentHandleException {
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        EnvironmentHandleException exception = new EnvironmentHandleException(cause);
        assertEquals(cause, exception.getCause());
        throw exception;
    }

    @Test (expected = EnvironmentHandleException.class)
    public void checkMessageAndCauseMethod()
            throws EnvironmentHandleException {
        String str = "test";
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        EnvironmentHandleException exception = new EnvironmentHandleException(str, cause);
        assertEquals(exception.getMessage(), str);
        assertEquals(exception.getCause(), cause);
        throw exception;
    }
}
