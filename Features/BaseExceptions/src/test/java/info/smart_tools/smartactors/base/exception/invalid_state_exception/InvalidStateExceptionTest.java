package info.smart_tools.smartactors.base.exception.invalid_state_exception;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test for {@link InvalidStateException}.
 */
public class InvalidStateExceptionTest {
    @Test(expected = InvalidStateException.class)
    public void checkMessageMethod()
            throws InvalidStateException {
        String str = "test";
        InvalidStateException exception = new InvalidStateException(str);
        assertEquals(exception.getMessage(), str);
        throw exception;
    }

    @Test(expected = InvalidStateException.class)
    public void checkCauseMethod()
            throws InvalidStateException {
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        InvalidStateException exception = new InvalidStateException(cause);
        assertEquals(cause, exception.getCause());
        throw exception;
    }

    @Test (expected = InvalidStateException.class)
    public void checkMessageAndCauseMethod()
            throws InvalidStateException {
        String str = "test";
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        InvalidStateException exception = new InvalidStateException(str, cause);
        assertEquals(exception.getMessage(), str);
        assertEquals(exception.getCause(), cause);
        throw exception;
    }
}
