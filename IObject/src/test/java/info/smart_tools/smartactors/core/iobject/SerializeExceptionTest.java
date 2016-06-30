package info.smart_tools.smartactors.core.iobject;

import info.smart_tools.smartactors.core.iobject.exception.SerializeException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for SerializeException
 */
public class SerializeExceptionTest {

    @Test(expected = SerializeException.class)
    public void checkMessageMethod()
            throws SerializeException {
        String str = "test";
        SerializeException exception = new SerializeException(str);
        assertEquals(exception.getMessage(), str);
        throw exception;
    }

    @Test (expected = SerializeException.class)
    public void checkCauseMethod()
            throws SerializeException {
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        SerializeException exception = new SerializeException(cause);
        assertEquals(cause, exception.getCause());
        throw exception;
    }

    @Test (expected = SerializeException.class)
    public void checkMessageAndCauseMethod()
            throws SerializeException {
        String str = "test";
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        SerializeException exception = new SerializeException(str, cause);
        assertEquals(exception.getMessage(), str);
        assertEquals(exception.getCause(), cause);
        throw exception;
    }
}
