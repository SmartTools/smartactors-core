package info.smart_tools.smartactors.launcher.interfaces.exception.iplugin;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link PluginCreatorInitException}
 */
public class PluginCreatorInitExceptionTest {
    @Test(expected = PluginCreatorInitException.class)
    public void checkMessageMethod()
            throws PluginCreatorInitException {
        String str = "test";
        PluginCreatorInitException exception = new PluginCreatorInitException(str);
        assertEquals(exception.getMessage(), str);
        throw exception;
    }

    @Test(expected = PluginCreatorInitException.class)
    public void checkCauseMethod()
            throws PluginCreatorInitException {
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        PluginCreatorInitException exception = new PluginCreatorInitException(cause);
        assertEquals(cause, exception.getCause());
        throw exception;
    }

    @Test (expected = PluginCreatorInitException.class)
    public void checkMessageAndCauseMethod()
            throws PluginCreatorInitException {
        String str = "test";
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        PluginCreatorInitException exception = new PluginCreatorInitException(str, cause);
        assertEquals(exception.getMessage(), str);
        assertEquals(exception.getCause(), cause);
        throw exception;
    }
}
