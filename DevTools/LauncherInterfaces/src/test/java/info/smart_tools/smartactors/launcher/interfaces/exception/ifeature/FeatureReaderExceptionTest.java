package info.smart_tools.smartactors.launcher.interfaces.exception.ifeature;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link FeatureReaderException}
 */
public class FeatureReaderExceptionTest {
    @Test(expected = FeatureReaderException.class)
    public void checkMessageMethod()
            throws FeatureReaderException {
        String str = "test";
        FeatureReaderException exception = new FeatureReaderException(str);
        assertEquals(exception.getMessage(), str);
        throw exception;
    }

    @Test(expected = FeatureReaderException.class)
    public void checkCauseMethod()
            throws FeatureReaderException {
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        FeatureReaderException exception = new FeatureReaderException(cause);
        assertEquals(cause, exception.getCause());
        throw exception;
    }

    @Test(expected = FeatureReaderException.class)
    public void checkMessageAndCauseMethod()
            throws FeatureReaderException {
        String str = "test";
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        FeatureReaderException exception = new FeatureReaderException(str, cause);
        assertEquals(exception.getMessage(), str);
        assertEquals(exception.getCause(), cause);
        throw exception;
    }
}
