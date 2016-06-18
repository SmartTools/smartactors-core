package info.smart_tools.smartactors.core.pool_guard;

import info.smart_tools.smartactors.core.pool_guard.exception.PoolGuardException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for PoolGuardException
 */
public class PoolGuardExceptionTest {
    @Test(expected = PoolGuardException.class)
    public void checkMessageMethod()
            throws PoolGuardException {
        String str = "test";
        PoolGuardException exception = new PoolGuardException(str);
        assertEquals(exception.getMessage(), str);
        throw exception;
    }

    @Test (expected = PoolGuardException.class)
    public void checkCauseMethod()
            throws PoolGuardException {
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        PoolGuardException exception = new PoolGuardException(cause);
        assertEquals(cause, exception.getCause());
        throw exception;
    }

    @Test (expected = PoolGuardException.class)
    public void checkMessageAndCauseMethod()
            throws PoolGuardException {
        String str = "test";
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        PoolGuardException exception = new PoolGuardException(str, cause);
        assertEquals(exception.getMessage(), str);
        assertEquals(exception.getCause(), cause);
        throw exception;
    }

}
