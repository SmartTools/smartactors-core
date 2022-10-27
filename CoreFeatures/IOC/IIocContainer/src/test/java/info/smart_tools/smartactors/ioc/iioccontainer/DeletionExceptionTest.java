package info.smart_tools.smartactors.ioc.iioccontainer;

import info.smart_tools.smartactors.ioc.exception.DeletionException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for DeletionException
 */
public class DeletionExceptionTest {

    @Test (expected = DeletionException.class)
    public void checkMessageMethod()
            throws DeletionException {
        String str = "test";
        DeletionException exception = new DeletionException(str);
        assertEquals(exception.getMessage(), str);
        throw exception;
    }

    @Test (expected = DeletionException.class)
    public void checkCauseMethod()
            throws DeletionException {
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        DeletionException exception = new DeletionException(cause);
        assertEquals(cause, exception.getCause());
        throw exception;
    }

    @Test (expected = DeletionException.class)
    public void checkMessageAndCauseMethod()
            throws DeletionException {
        String str = "test";
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        DeletionException exception = new DeletionException(str, cause);
        assertEquals(exception.getMessage(), str);
        assertEquals(exception.getCause(), cause);
        throw exception;
    }
}
