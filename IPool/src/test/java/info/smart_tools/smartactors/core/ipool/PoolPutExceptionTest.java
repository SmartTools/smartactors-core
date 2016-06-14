package info.smart_tools.smartactors.core.ipool;


import info.smart_tools.smartactors.core.ipool.exception.PoolPutException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for PoolPutException
 */
public class PoolPutExceptionTest {
    @Test(expected = PoolPutException.class)
    public void checkMessageMethod() throws PoolPutException {
        String str = "test";
        PoolPutException exception = new PoolPutException(str);
        assertEquals(exception.getMessage(), str);
        throw exception;
    }

    @Test(expected = PoolPutException.class)
    public void checkCauseMethod()
            throws PoolPutException {
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        PoolPutException exception = new PoolPutException(cause);
        assertEquals(cause, exception.getCause());
        throw exception;
    }

    @Test(expected = PoolPutException.class)
    public void checkMessageAndCauseMethod()
            throws PoolPutException {
        String str = "test";
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        PoolPutException exception = new PoolPutException(str, cause);
        assertEquals(exception.getMessage(), str);
        assertEquals(exception.getCause(), cause);
        throw exception;
    }
}
