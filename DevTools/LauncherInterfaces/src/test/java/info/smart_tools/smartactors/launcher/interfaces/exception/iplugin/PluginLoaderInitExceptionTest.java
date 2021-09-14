package info.smart_tools.smartactors.launcher.interfaces.exception.iplugin;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link PluginLoaderInitException}
 */
public class PluginLoaderInitExceptionTest {
    @Test(expected = PluginLoaderInitException.class)
    public void checkMessageMethod()
            throws PluginLoaderInitException {
        String str = "test";
        PluginLoaderInitException exception = new PluginLoaderInitException(str);
        assertEquals(exception.getMessage(), str);
        throw exception;
    }

    @Test(expected = PluginLoaderInitException.class)
    public void checkCauseMethod()
            throws PluginLoaderInitException {
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        PluginLoaderInitException exception = new PluginLoaderInitException(cause);
        assertEquals(cause, exception.getCause());
        throw exception;
    }

    @Test (expected = PluginLoaderInitException.class)
    public void checkMessageAndCauseMethod()
            throws PluginLoaderInitException {
        String str = "test";
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        PluginLoaderInitException exception = new PluginLoaderInitException(str, cause);
        assertEquals(exception.getMessage(), str);
        assertEquals(exception.getCause(), cause);
        throw exception;
    }
}
