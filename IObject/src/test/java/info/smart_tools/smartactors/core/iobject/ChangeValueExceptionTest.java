package info.smart_tools.smartactors.core.iobject;

import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for ChangeValueException
 */
public class ChangeValueExceptionTest {

    @Test(expected = ChangeValueException.class)
    public void checkMessageMethod()
            throws ChangeValueException {
        String str = "test";
        ChangeValueException exception = new ChangeValueException(str);
        assertEquals(exception.getMessage(), str);
        throw exception;
    }

    @Test (expected = ChangeValueException.class)
    public void checkCauseMethod()
            throws ChangeValueException {
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        ChangeValueException exception = new ChangeValueException(cause);
        assertEquals(cause, exception.getCause());
        throw exception;
    }

    @Test (expected = ChangeValueException.class)
    public void checkMessageAndCauseMethod()
            throws ChangeValueException {
        String str = "test";
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        ChangeValueException exception = new ChangeValueException(str, cause);
        assertEquals(exception.getMessage(), str);
        assertEquals(exception.getCause(), cause);
        throw exception;
    }
}
