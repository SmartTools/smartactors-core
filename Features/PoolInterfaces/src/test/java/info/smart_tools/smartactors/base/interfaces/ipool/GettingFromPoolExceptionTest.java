package info.smart_tools.smartactors.base.interfaces.ipool;

import info.smart_tools.smartactors.base.interfaces.ipool.exception.GettingFromPoolException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for GettingFromPoolException
 */
public class GettingFromPoolExceptionTest {
    @Test(expected = GettingFromPoolException.class)
    public void checkMessageMethod() throws GettingFromPoolException {
        String str = "test";
        GettingFromPoolException exception = new GettingFromPoolException(str);
        assertEquals(exception.getMessage(), str);
        throw exception;
    }

    @Test(expected = GettingFromPoolException.class)
    public void checkCauseMethod()
            throws GettingFromPoolException {
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        GettingFromPoolException exception = new GettingFromPoolException(cause);
        assertEquals(cause, exception.getCause());
        throw exception;
    }

    @Test(expected = GettingFromPoolException.class)
    public void checkMessageAndCauseMethod()
            throws GettingFromPoolException {
        String str = "test";
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        GettingFromPoolException exception = new GettingFromPoolException(str, cause);
        assertEquals(exception.getMessage(), str);
        assertEquals(exception.getCause(), cause);
        throw exception;
    }
}
