package info.smart_tools.smartactors.scope.iscope_provider_container;

import info.smart_tools.smartactors.scope.iscope_provider_container.exception.ScopeProviderException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for ScopeProviderException
 */
public class ScopeProviderExceptionTest {
    @Test(expected = ScopeProviderException.class)
    public void checkMessageMethod()
            throws ScopeProviderException {
        String str = "test";
        ScopeProviderException exception = new ScopeProviderException(str);
        assertEquals(exception.getMessage(), str);
        throw exception;
    }

    @Test (expected = ScopeProviderException.class)
    public void checkCauseMethod()
            throws ScopeProviderException {
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        ScopeProviderException exception = new ScopeProviderException(cause);
        assertEquals(cause, exception.getCause());
        throw exception;
    }

    @Test (expected = ScopeProviderException.class)
    public void checkMessageAndCauseMethod()
            throws ScopeProviderException {
        String str = "test";
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        ScopeProviderException exception = new ScopeProviderException(str, cause);
        assertEquals(exception.getMessage(), str);
        assertEquals(exception.getCause(), cause);
        throw exception;
    }
}
