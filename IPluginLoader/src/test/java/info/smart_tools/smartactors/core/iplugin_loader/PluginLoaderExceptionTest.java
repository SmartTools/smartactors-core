package info.smart_tools.smartactors.core.iplugin_loader;

import info.smart_tools.smartactors.core.iplugin_loader.exception.PluginLoaderException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for PluginLoaderException
 */
public class PluginLoaderExceptionTest {
    @Test(expected = PluginLoaderException.class)
    public void checkMessageMethod()
            throws PluginLoaderException {
        String str = "test";
        PluginLoaderException exception = new PluginLoaderException(str);
        assertEquals(exception.getMessage(), str);
        throw exception;
    }

    @Test(expected = PluginLoaderException.class)
    public void checkCauseMethod()
            throws PluginLoaderException {
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        PluginLoaderException exception = new PluginLoaderException(cause);
        assertEquals(cause, exception.getCause());
        throw exception;
    }

    @Test (expected = PluginLoaderException.class)
    public void checkMessageAndCauseMethod()
            throws PluginLoaderException {
        String str = "test";
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        PluginLoaderException exception = new PluginLoaderException(str, cause);
        assertEquals(exception.getMessage(), str);
        assertEquals(exception.getCause(), cause);
        throw exception;
    }
}
