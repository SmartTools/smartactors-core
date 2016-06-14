package info.smart_tools.smartactors.core.itask.exception;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test for {@link TaskExecutionException}
 */
public class TaskExecutionExceptionTest {
    @Test(expected = TaskExecutionException.class)
    public void checkMessageMethod()
            throws TaskExecutionException {
        String str = "test";
        TaskExecutionException exception = new TaskExecutionException(str);
        assertEquals(exception.getMessage(), str);
        throw exception;
    }

    @Test(expected = TaskExecutionException.class)
    public void checkCauseMethod()
            throws TaskExecutionException {
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        TaskExecutionException exception = new TaskExecutionException(cause);
        assertEquals(cause, exception.getCause());
        throw exception;
    }

    @Test (expected = TaskExecutionException.class)
    public void checkMessageAndCauseMethod()
            throws TaskExecutionException {
        String str = "test";
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        TaskExecutionException exception = new TaskExecutionException(str, cause);
        assertEquals(exception.getMessage(), str);
        assertEquals(exception.getCause(), cause);
        throw exception;
    }
}
