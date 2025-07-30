package info.smart_tools.smartactors.message_bus.interfaces.imessage_bus_container;

import info.smart_tools.smartactors.message_bus.interfaces.imessage_bus_container.exception.SendingMessageException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for PluginCreationException
 */
public class SendingMessageExceptionTest {
    @Test(expected = SendingMessageException.class)
    public void checkMessageMethod()
            throws SendingMessageException {
        String str = "test";
        SendingMessageException exception = new SendingMessageException(str);
        assertEquals(exception.getMessage(), str);
        throw exception;
    }

    @Test(expected = SendingMessageException.class)
    public void checkCauseMethod()
            throws SendingMessageException {
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        SendingMessageException exception = new SendingMessageException(cause);
        assertEquals(cause, exception.getCause());
        throw exception;
    }

    @Test (expected = SendingMessageException.class)
    public void checkMessageAndCauseMethod()
            throws SendingMessageException {
        String str = "test";
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        SendingMessageException exception = new SendingMessageException(str, cause);
        assertEquals(exception.getMessage(), str);
        assertEquals(exception.getCause(), cause);
        throw exception;
    }
}
