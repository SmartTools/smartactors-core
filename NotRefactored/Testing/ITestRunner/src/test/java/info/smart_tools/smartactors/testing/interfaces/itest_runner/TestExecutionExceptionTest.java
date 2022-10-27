package info.smart_tools.smartactors.testing.interfaces.itest_runner;

import info.smart_tools.smartactors.testing.interfaces.itest_runner.exception.TestExecutionException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link TestExecutionException}
 */
public class TestExecutionExceptionTest {

    @Test(expected = TestExecutionException.class)
    public void checkMessageMethod()
            throws TestExecutionException {
        String str = "test";
        TestExecutionException exception = new TestExecutionException(str);
        assertEquals(exception.getMessage(), str);
        throw exception;
    }

    @Test (expected = TestExecutionException.class)
    public void checkCauseMethod()
            throws TestExecutionException {
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        TestExecutionException exception = new TestExecutionException(cause);
        assertEquals(cause, exception.getCause());
        throw exception;
    }

    @Test (expected = TestExecutionException.class)
    public void checkMessageAndCauseMethod()
            throws TestExecutionException {
        String str = "test";
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        TestExecutionException exception = new TestExecutionException(str, cause);
        assertEquals(exception.getMessage(), str);
        assertEquals(exception.getCause(), cause);
        throw exception;
    }
}
