package info.smart_tools.smartactors.launcher.interfaces.exception.iproperties_reader;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link PropertiesReaderException}
 */
public class PropertiesReaderExceptionTest {
    @Test(expected = PropertiesReaderException.class)
    public void checkMessageMethod()
            throws PropertiesReaderException {
        String str = "test";
        PropertiesReaderException exception = new PropertiesReaderException(str);
        assertEquals(exception.getMessage(), str);
        throw exception;
    }

    @Test(expected = PropertiesReaderException.class)
    public void checkCauseMethod()
            throws PropertiesReaderException {
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        PropertiesReaderException exception = new PropertiesReaderException(cause);
        assertEquals(cause, exception.getCause());
        throw exception;
    }

    @Test (expected = PropertiesReaderException.class)
    public void checkMessageAndCauseMethod()
            throws PropertiesReaderException {
        String str = "test";
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        PropertiesReaderException exception = new PropertiesReaderException(str, cause);
        assertEquals(exception.getMessage(), str);
        assertEquals(exception.getCause(), cause);
        throw exception;
    }
}
