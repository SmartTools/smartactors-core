package info.smart_tools.smartactors.launcher.interfaces.exception.ifeature;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link FeatureSortingException}
 */
public class FeatureSortingExceptionTest {
    @Test(expected = FeatureSortingException.class)
    public void checkMessageMethod()
            throws FeatureSortingException {
        String str = "test";
        FeatureSortingException exception = new FeatureSortingException(str);
        assertEquals(exception.getMessage(), str);
        throw exception;
    }

    @Test(expected = FeatureSortingException.class)
    public void checkCauseMethod()
            throws FeatureSortingException {
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        FeatureSortingException exception = new FeatureSortingException(cause);
        assertEquals(cause, exception.getCause());
        throw exception;
    }

    @Test (expected = FeatureSortingException.class)
    public void checkMessageAndCauseMethod()
            throws FeatureSortingException {
        String str = "test";
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        FeatureSortingException exception = new FeatureSortingException(str, cause);
        assertEquals(exception.getMessage(), str);
        assertEquals(exception.getCause(), cause);
        throw exception;
    }
}
