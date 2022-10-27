package info.smart_tools.smartactors.scope.iscope;

import info.smart_tools.smartactors.scope.iscope.exception.ScopeException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for ScopeException
 */
public class ScopeExceptionTest {
    @Test(expected = ScopeException.class)
    public void checkMessageMethod()
            throws ScopeException {
        String str = "test";
        ScopeException exception = new ScopeException(str);
        assertEquals(exception.getMessage(), str);
        throw exception;
    }

    @Test(expected = ScopeException.class)
    public void checkCauseMethod()
            throws ScopeException {
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        ScopeException exception = new ScopeException(cause);
        assertEquals(cause, exception.getCause());
        throw exception;
    }

    @Test (expected = ScopeException.class)
    public void checkMessageAndCauseMethod()
            throws ScopeException {
        String str = "test";
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        ScopeException exception = new ScopeException(str, cause);
        assertEquals(exception.getMessage(), str);
        assertEquals(exception.getCause(), cause);
        throw exception;
    }
}
