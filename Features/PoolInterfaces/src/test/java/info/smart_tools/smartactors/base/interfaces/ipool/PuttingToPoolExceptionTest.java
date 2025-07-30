package info.smart_tools.smartactors.base.interfaces.ipool;


import info.smart_tools.smartactors.base.interfaces.ipool.exception.PuttingToPoolException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for PuttingToPoolException
 */
public class PuttingToPoolExceptionTest {
    @Test(expected = PuttingToPoolException.class)
    public void checkMessageMethod() throws PuttingToPoolException {
        String str = "test";
        PuttingToPoolException exception = new PuttingToPoolException(str);
        assertEquals(exception.getMessage(), str);
        throw exception;
    }

    @Test(expected = PuttingToPoolException.class)
    public void checkCauseMethod()
            throws PuttingToPoolException {
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        PuttingToPoolException exception = new PuttingToPoolException(cause);
        assertEquals(cause, exception.getCause());
        throw exception;
    }

    @Test(expected = PuttingToPoolException.class)
    public void checkMessageAndCauseMethod()
            throws PuttingToPoolException {
        String str = "test";
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        PuttingToPoolException exception = new PuttingToPoolException(str, cause);
        assertEquals(exception.getMessage(), str);
        assertEquals(exception.getCause(), cause);
        throw exception;
    }
}
