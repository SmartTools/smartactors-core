package info.smart_tools.smartactors.feature_management.unzip_feature_actor;

import info.smart_tools.smartactors.feature_management.unzip_feature_actor.exception.UnzipFeatureException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for UnzipFeatureException
 */
public class UnzipFeatureExceptionTest {

    @Test (expected = UnzipFeatureException.class)
    public void checkMessageMethod()
            throws UnzipFeatureException {
        String str = "test";
        UnzipFeatureException exception = new UnzipFeatureException(str);
        assertEquals(exception.getMessage(), str);
        throw exception;
    }

    @Test (expected = UnzipFeatureException.class)
    public void checkCauseMethod()
            throws UnzipFeatureException {
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        UnzipFeatureException exception = new UnzipFeatureException(cause);
        assertEquals(cause, exception.getCause());
        throw exception;
    }

    @Test (expected = UnzipFeatureException.class)
    public void checkMessageAndCauseMethod()
            throws UnzipFeatureException {
        String str = "test";
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        UnzipFeatureException exception = new UnzipFeatureException(str, cause);
        assertEquals(exception.getMessage(), str);
        assertEquals(exception.getCause(), cause);
        throw exception;
    }

}
