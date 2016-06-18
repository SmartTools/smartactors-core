package info.smart_tools.smartactors.core.ifeature_manager.exception;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Test for {@link FeatureManagementException}.
 */
public class FeatureManagementExceptionTest {
    @Test(expected = FeatureManagementException.class)
    public void checkMessageMethod()
            throws FeatureManagementException {
        String str = "test";
        FeatureManagementException exception = new FeatureManagementException(str);
        assertEquals(exception.getMessage(), str);
        throw exception;
    }

    @Test(expected = FeatureManagementException.class)
    public void checkCauseMethod()
            throws FeatureManagementException {
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        FeatureManagementException exception = new FeatureManagementException(cause);
        assertEquals(cause, exception.getCause());
        throw exception;
    }

    @Test (expected = FeatureManagementException.class)
    public void checkMessageAndCauseMethod()
            throws FeatureManagementException {
        String str = "test";
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        FeatureManagementException exception = new FeatureManagementException(str, cause);
        assertEquals(exception.getMessage(), str);
        assertEquals(exception.getCause(), cause);
        throw exception;
    }
}
