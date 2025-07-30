package info.smart_tools.smartactors.message_processing_interfaces.iwrapper_generator;

import info.smart_tools.smartactors.message_processing_interfaces.iwrapper_generator.exception.WrapperGeneratorException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for WrapperGeneratorException
 */
public class WrapperGeneratorExceptionTest {

    @Test(expected = WrapperGeneratorException.class)
    public void checkMessageMethod()
            throws WrapperGeneratorException {
        String str = "test";
        WrapperGeneratorException exception = new WrapperGeneratorException(str);
        assertEquals(exception.getMessage(), str);
        throw exception;
    }

    @Test (expected = WrapperGeneratorException.class)
    public void checkCauseMethod()
            throws WrapperGeneratorException {
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        WrapperGeneratorException exception = new WrapperGeneratorException(cause);
        assertEquals(cause, exception.getCause());
        throw exception;
    }

    @Test (expected = WrapperGeneratorException.class)
    public void checkMessageAndCauseMethod()
            throws WrapperGeneratorException {
        String str = "test";
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        WrapperGeneratorException exception = new WrapperGeneratorException(str, cause);
        assertEquals(exception.getMessage(), str);
        assertEquals(exception.getCause(), cause);
        throw exception;
    }
}
