package info.smart_tools.smartactors.core.ipool;

import info.smart_tools.smartactors.core.ipool.exception.PoolTakeException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for PoolTakeException
 */
public class PoolTakeExceptionTest {
    @Test(expected = PoolTakeException.class)
    public void checkMessageMethod() throws PoolTakeException {
        String str = "test";
        PoolTakeException exception = new PoolTakeException(str);
        assertEquals(exception.getMessage(), str);
        throw exception;
    }

    @Test(expected = PoolTakeException.class)
    public void checkCauseMethod()
            throws PoolTakeException {
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        PoolTakeException exception = new PoolTakeException(cause);
        assertEquals(cause, exception.getCause());
        throw exception;
    }

    @Test(expected = PoolTakeException.class)
    public void checkMessageAndCauseMethod()
            throws PoolTakeException {
        String str = "test";
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        PoolTakeException exception = new PoolTakeException(str, cause);
        assertEquals(exception.getMessage(), str);
        assertEquals(exception.getCause(), cause);
        throw exception;
    }
}
