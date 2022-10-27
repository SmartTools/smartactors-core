package info.smart_tools.smartactors.base.exception.initialization_exception;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link InitializationException}.
 */
public class InitializationExceptionTest {
    @Test(expected = InitializationException.class)
    public void checkMessageMethod()
            throws InitializationException {
        String str = "test";
        InitializationException exception = new InitializationException(str);
        assertEquals(exception.getMessage(), str);
        throw exception;
    }

    @Test(expected = InitializationException.class)
    public void checkCauseMethod()
            throws InitializationException {
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        InitializationException exception = new InitializationException(cause);
        assertEquals(cause, exception.getCause());
        throw exception;
    }

    @Test (expected = InitializationException.class)
    public void checkMessageAndCauseMethod()
            throws InitializationException {
        String str = "test";
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        InitializationException exception = new InitializationException(str, cause);
        assertEquals(exception.getMessage(), str);
        assertEquals(exception.getCause(), cause);
        throw exception;
    }
}
