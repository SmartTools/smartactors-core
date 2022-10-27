package info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin;

import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for PluginException
 */
public class PluginExceptionTest {
    @Test(expected = PluginException.class)
    public void checkMessageMethod()
            throws PluginException {
        String str = "test";
        PluginException exception = new PluginException(str);
        assertEquals(exception.getMessage(), str);
        throw exception;
    }

    @Test(expected = PluginException.class)
    public void checkCauseMethod()
            throws PluginException {
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        PluginException exception = new PluginException(cause);
        assertEquals(cause, exception.getCause());
        throw exception;
    }

    @Test (expected = PluginException.class)
    public void checkMessageAndCauseMethod()
            throws PluginException {
        String str = "test";
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        PluginException exception = new PluginException(str, cause);
        assertEquals(exception.getMessage(), str);
        assertEquals(exception.getCause(), cause);
        throw exception;
    }
}
