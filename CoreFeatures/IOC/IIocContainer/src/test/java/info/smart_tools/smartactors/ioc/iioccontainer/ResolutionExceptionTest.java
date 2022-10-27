package info.smart_tools.smartactors.ioc.iioccontainer;

import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for ResolutionException
 */
public class ResolutionExceptionTest {

    @Test (expected = ResolutionException.class)
    public void checkMessageMethod()
            throws ResolutionException {
        String str = "test";
        ResolutionException exception = new ResolutionException(str);
        assertEquals(exception.getMessage(), str);
        throw exception;
    }

    @Test (expected = ResolutionException.class)
    public void checkCauseMethod()
            throws ResolutionException {
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        ResolutionException exception = new ResolutionException(cause);
        assertEquals(cause, exception.getCause());
        throw exception;
    }

    @Test (expected = ResolutionException.class)
    public void checkMessageAndCauseMethod()
            throws ResolutionException {
        String str = "test";
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        ResolutionException exception = new ResolutionException(str, cause);
        assertEquals(exception.getMessage(), str);
        assertEquals(exception.getCause(), cause);
        throw exception;
    }

    @Test (expected = ResolutionException.class)
    public void checkOfResolutionMethod()
            throws ResolutionException {
        Object[] args = new Object[]{1, 2, 3};
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        ResolutionException exception = ResolutionException.ofResolution(Integer.class, args, cause);
        throw exception;
    }
}
