package info.smart_tools.smartactors.core.iscope;

import info.smart_tools.smartactors.core.iscope.exception.ScopeFactoryException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for ScopeFactoryExceptionTest
 */
public class ScopeFactoryExceptionTest {

    @Test(expected = ScopeFactoryException.class)
    public void checkMessageMethod()
            throws ScopeFactoryException {
        String str = "test";
        ScopeFactoryException exception = new ScopeFactoryException(str);
        assertEquals(exception.getMessage(), str);
        throw exception;
    }

    @Test(expected = ScopeFactoryException.class)
    public void checkCauseMethod()
            throws ScopeFactoryException {
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        ScopeFactoryException exception = new ScopeFactoryException(cause);
        assertEquals(cause, exception.getCause());
        throw exception;
    }

    @Test (expected = ScopeFactoryException.class)
    public void checkMessageAndCauseMethod()
            throws ScopeFactoryException {
        String str = "test";
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        ScopeFactoryException exception = new ScopeFactoryException(str, cause);
        assertEquals(exception.getMessage(), str);
        assertEquals(exception.getCause(), cause);
        throw exception;
    }
}
