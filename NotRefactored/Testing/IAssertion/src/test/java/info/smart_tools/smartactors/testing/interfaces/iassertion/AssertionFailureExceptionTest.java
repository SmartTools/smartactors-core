package info.smart_tools.smartactors.testing.interfaces.iassertion;

import info.smart_tools.smartactors.testing.interfaces.iassertion.exception.AssertionFailureException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link AssertionFailureException}
 */
public class AssertionFailureExceptionTest {

    @Test(expected = AssertionFailureException.class)
    public void checkMessageMethod()
            throws AssertionFailureException {
        String str = "test";
        AssertionFailureException exception = new AssertionFailureException(str);
        assertEquals(exception.getMessage(), str);
        throw exception;
    }

    @Test (expected = AssertionFailureException.class)
    public void checkCauseMethod()
            throws AssertionFailureException {
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        AssertionFailureException exception = new AssertionFailureException(cause);
        assertEquals(cause, exception.getCause());
        throw exception;
    }

    @Test (expected = AssertionFailureException.class)
    public void checkMessageAndCauseMethod()
            throws AssertionFailureException {
        String str = "test";
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        AssertionFailureException exception = new AssertionFailureException(str, cause);
        assertEquals(exception.getMessage(), str);
        assertEquals(exception.getCause(), cause);
        throw exception;
    }
}
