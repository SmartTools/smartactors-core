package info.smart_tools.smartactors.launcher.interfaces.exception.core;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link FeatureLoaderException}
 */
public class FeatureLoaderExceptionTest {
    @Test(expected = FeatureLoaderException.class)
    public void checkMessageMethod()
            throws FeatureLoaderException {
        String str = "test";
        FeatureLoaderException exception = new FeatureLoaderException(str);
        assertEquals(exception.getMessage(), str);
        throw exception;
    }

    @Test(expected = FeatureLoaderException.class)
    public void checkCauseMethod()
            throws FeatureLoaderException {
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        FeatureLoaderException exception = new FeatureLoaderException(cause);
        assertEquals(cause, exception.getCause());
        throw exception;
    }

    @Test (expected = FeatureLoaderException.class)
    public void checkMessageAndCauseMethod()
            throws FeatureLoaderException {
        String str = "test";
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        FeatureLoaderException exception = new FeatureLoaderException(str, cause);
        assertEquals(exception.getMessage(), str);
        assertEquals(exception.getCause(), cause);
        throw exception;
    }
}
