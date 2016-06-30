package info.smart_tools.smartactors.core.iobject;

import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for ReadValueException
 */
public class ReadValueExceptionTest {

    @Test(expected = ReadValueException.class)
    public void checkMessageMethod()
            throws ReadValueException {
        String str = "test";
        ReadValueException exception = new ReadValueException(str);
        assertEquals(exception.getMessage(), str);
        throw exception;
    }

    @Test (expected = ReadValueException.class)
    public void checkCauseMethod()
            throws ReadValueException {
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        ReadValueException exception = new ReadValueException(cause);
        assertEquals(cause, exception.getCause());
        throw exception;
    }

    @Test (expected = ReadValueException.class)
    public void checkMessageAndCauseMethod()
            throws ReadValueException {
        String str = "test";
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        ReadValueException exception = new ReadValueException(str, cause);
        assertEquals(exception.getMessage(), str);
        assertEquals(exception.getCause(), cause);
        throw exception;
    }
}
