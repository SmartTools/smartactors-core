package info.smart_tools.smartactors.iobject.iobject;

import info.smart_tools.smartactors.iobject.iobject.exception.DeleteValueException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for ChangeValueException
 */
public class DeleteValueExceptionTest {

    @Test(expected = DeleteValueException.class)
    public void checkMessageMethod()
            throws DeleteValueException {
        String str = "test";
        DeleteValueException exception = new DeleteValueException(str);
        assertEquals(exception.getMessage(), str);
        throw exception;
    }

    @Test (expected = DeleteValueException.class)
    public void checkCauseMethod()
            throws DeleteValueException {
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        DeleteValueException exception = new DeleteValueException(cause);
        assertEquals(cause, exception.getCause());
        throw exception;
    }

    @Test (expected = DeleteValueException.class)
    public void checkMessageAndCauseMethod()
            throws DeleteValueException {
        String str = "test";
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        DeleteValueException exception = new DeleteValueException(str, cause);
        assertEquals(exception.getMessage(), str);
        assertEquals(exception.getCause(), cause);
        throw exception;
    }
}
