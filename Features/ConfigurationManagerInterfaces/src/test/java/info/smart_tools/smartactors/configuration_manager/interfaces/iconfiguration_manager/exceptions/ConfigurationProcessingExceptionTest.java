package info.smart_tools.smartactors.configuration_manager.interfaces.iconfiguration_manager.exceptions;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link ConfigurationProcessingException}.
 */
public class ConfigurationProcessingExceptionTest {
    @Test(expected = ConfigurationProcessingException.class)
    public void checkMessageMethod()
            throws ConfigurationProcessingException {
        String str = "test";
        ConfigurationProcessingException exception = new ConfigurationProcessingException(str);
        assertEquals(exception.getMessage(), str);
        throw exception;
    }

    @Test(expected = ConfigurationProcessingException.class)
    public void checkCauseMethod()
            throws ConfigurationProcessingException {
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        ConfigurationProcessingException exception = new ConfigurationProcessingException(cause);
        assertEquals(cause, exception.getCause());
        throw exception;
    }

    @Test(expected = ConfigurationProcessingException.class)
    public void checkMessageAndCauseMethod()
            throws ConfigurationProcessingException {
        String str = "test";
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        ConfigurationProcessingException exception = new ConfigurationProcessingException(str, cause);
        assertEquals(exception.getMessage(), str);
        assertEquals(exception.getCause(), cause);
        throw exception;
    }
}
