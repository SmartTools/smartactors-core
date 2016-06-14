package info.smart_tools.smartactors.core.iioccontainer;

import info.smart_tools.smartactors.core.iioccontainer.exception.RegistrationException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for RegistrationException
 */
public class RegistrationExceptionTest {

    @Test(expected = RegistrationException.class)
    public void checkMessageMethod()
            throws RegistrationException {
        String str = "test";
        RegistrationException exception = new RegistrationException(str);
        assertEquals(exception.getMessage(), str);
        throw exception;
    }

    @Test (expected = RegistrationException.class)
    public void checkCauseMethod()
            throws RegistrationException {
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        RegistrationException exception = new RegistrationException(cause);
        assertEquals(cause, exception.getCause());
        throw exception;
    }

    @Test (expected = RegistrationException.class)
    public void checkMessageAndCauseMethod()
            throws RegistrationException {
        String str = "test";
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        RegistrationException exception = new RegistrationException(str, cause);
        assertEquals(exception.getMessage(), str);
        assertEquals(exception.getCause(), cause);
        throw exception;
    }
}
