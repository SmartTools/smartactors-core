package info.smart_tools.smartactors.message_processing_interfaces.ireceiver_generator;

import info.smart_tools.smartactors.message_processing_interfaces.ireceiver_generator.exception.ReceiverGeneratorException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for ReceiverGeneratorException
 */
public class ReceiverGeneratorExceptionTest {

    @Test(expected = ReceiverGeneratorException.class)
    public void checkMessageMethod()
            throws ReceiverGeneratorException {
        String str = "test";
        ReceiverGeneratorException exception = new ReceiverGeneratorException(str);
        assertEquals(exception.getMessage(), str);
        throw exception;
    }

    @Test (expected = ReceiverGeneratorException.class)
    public void checkCauseMethod()
            throws ReceiverGeneratorException {
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        ReceiverGeneratorException exception = new ReceiverGeneratorException(cause);
        assertEquals(cause, exception.getCause());
        throw exception;
    }

    @Test (expected = ReceiverGeneratorException.class)
    public void checkMessageAndCauseMethod()
            throws ReceiverGeneratorException {
        String str = "test";
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        ReceiverGeneratorException exception = new ReceiverGeneratorException(str, cause);
        assertEquals(exception.getMessage(), str);
        assertEquals(exception.getCause(), cause);
        throw exception;
    }
}
