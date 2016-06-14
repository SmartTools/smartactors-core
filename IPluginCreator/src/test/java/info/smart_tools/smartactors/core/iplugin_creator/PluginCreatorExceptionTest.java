package info.smart_tools.smartactors.core.iplugin_creator;

import info.smart_tools.smartactors.core.iplugin_creator.exception.PluginCreationException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for PluginCreationException
 */
public class PluginCreatorExceptionTest {
    @Test(expected = PluginCreationException.class)
    public void checkMessageMethod()
            throws PluginCreationException {
        String str = "test";
        PluginCreationException exception = new PluginCreationException(str);
        assertEquals(exception.getMessage(), str);
        throw exception;
    }

    @Test(expected = PluginCreationException.class)
    public void checkCauseMethod()
            throws PluginCreationException {
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        PluginCreationException exception = new PluginCreationException(cause);
        assertEquals(cause, exception.getCause());
        throw exception;
    }

    @Test (expected = PluginCreationException.class)
    public void checkMessageAndCauseMethod()
            throws PluginCreationException {
        String str = "test";
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        PluginCreationException exception = new PluginCreationException(str, cause);
        assertEquals(exception.getMessage(), str);
        assertEquals(exception.getCause(), cause);
        throw exception;
    }
}
