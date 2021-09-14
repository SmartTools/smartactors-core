package info.smart_tools.smartactors.launcher.interfaces.exception.iobject_mapper;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link ReadJsonException}
 */
public class ReadJsonExceptionTest {
    @Test(expected = ReadJsonException.class)
    public void checkMessageMethod()
            throws ReadJsonException {
        String str = "test";
        ReadJsonException exception = new ReadJsonException(str);
        assertEquals(exception.getMessage(), str);
        throw exception;
    }

    @Test(expected = ReadJsonException.class)
    public void checkCauseMethod()
            throws ReadJsonException {
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        ReadJsonException exception = new ReadJsonException(cause);
        assertEquals(cause, exception.getCause());
        throw exception;
    }

    @Test(expected = ReadJsonException.class)
    public void checkMessageAndCauseMethod()
            throws ReadJsonException {
        String str = "test";
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        ReadJsonException exception = new ReadJsonException(str, cause);
        assertEquals(exception.getMessage(), str);
        assertEquals(exception.getCause(), cause);
        throw exception;
    }
}
