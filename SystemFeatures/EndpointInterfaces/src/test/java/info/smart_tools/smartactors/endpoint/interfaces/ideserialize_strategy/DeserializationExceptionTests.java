package info.smart_tools.smartactors.endpoint.interfaces.ideserialize_strategy;

import info.smart_tools.smartactors.endpoint.interfaces.ideserialize_strategy.exceptions.DeserializationException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DeserializationExceptionTests {
    @Test(expected = DeserializationException.class)
    public void checkMessageMethod()
            throws DeserializationException {
        String str = "test";
        DeserializationException exception = new DeserializationException(str);
        assertEquals(exception.getMessage(), str);
        throw exception;
    }

    @Test(expected = DeserializationException.class)
    public void checkCauseMethod()
            throws DeserializationException {
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        DeserializationException exception = new DeserializationException(cause);
        assertEquals(cause, exception.getCause());
        throw exception;
    }

    @Test (expected = DeserializationException.class)
    public void checkMessageAndCauseMethod()
            throws DeserializationException {
        String str = "test";
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        DeserializationException exception = new DeserializationException(str, cause);
        assertEquals(exception.getMessage(), str);
        assertEquals(exception.getCause(), cause);
        throw exception;
    }
}
