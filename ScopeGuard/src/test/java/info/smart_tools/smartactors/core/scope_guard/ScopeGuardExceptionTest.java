package info.smart_tools.smartactors.core.scope_guard;

import info.smart_tools.smartactors.core.scope_guard.exception.ScopeGuardException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for ScopeGuardException
 */
public class ScopeGuardExceptionTest {

    @Test(expected = ScopeGuardException.class)
    public void checkMessageMethod()
            throws ScopeGuardException {
        String str = "test";
        ScopeGuardException exception = new ScopeGuardException(str);
        assertEquals(exception.getMessage(), str);
        throw exception;
    }

    @Test (expected = ScopeGuardException.class)
    public void checkCauseMethod()
            throws ScopeGuardException {
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        ScopeGuardException exception = new ScopeGuardException(cause);
        assertEquals(cause, exception.getCause());
        throw exception;
    }

    @Test (expected = ScopeGuardException.class)
    public void checkMessageAndCauseMethod()
            throws ScopeGuardException {
        String str = "test";
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        ScopeGuardException exception = new ScopeGuardException(str, cause);
        assertEquals(exception.getMessage(), str);
        assertEquals(exception.getCause(), cause);
        throw exception;
    }
}
