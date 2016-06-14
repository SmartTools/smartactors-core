package info.smart_tools.smartactors.core.invalid_argument_exception;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for ScopeException
 */
public class InvalidArgumentExceptionTest {
    @Test(expected = InvalidArgumentException.class)
    public void checkMessageMethod()
            throws InvalidArgumentException {
        String str = "test";
        InvalidArgumentException exception = new InvalidArgumentException(str);
        assertEquals(exception.getMessage(), str);
        throw exception;
    }

    @Test(expected = InvalidArgumentException.class)
    public void checkCauseMethod()
            throws InvalidArgumentException {
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        InvalidArgumentException exception = new InvalidArgumentException(cause);
        assertEquals(cause, exception.getCause());
        throw exception;
    }

    @Test (expected = InvalidArgumentException.class)
    public void checkMessageAndCauseMethod()
            throws InvalidArgumentException {
        String str = "test";
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        InvalidArgumentException exception = new InvalidArgumentException(str, cause);
        assertEquals(exception.getMessage(), str);
        assertEquals(exception.getCause(), cause);
        throw exception;
    }
}
