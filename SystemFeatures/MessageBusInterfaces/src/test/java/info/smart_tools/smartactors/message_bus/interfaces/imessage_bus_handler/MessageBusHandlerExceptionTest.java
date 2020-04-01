package info.smart_tools.smartactors.message_bus.interfaces.imessage_bus_handler;

import info.smart_tools.smartactors.message_bus.interfaces.imessage_bus_handler.exception.MessageBusHandlerException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for MessageBusHandlerException
 */
public class MessageBusHandlerExceptionTest {

    @Test (expected = MessageBusHandlerException.class)
    public void checkMessageMethod()
            throws MessageBusHandlerException {
        String str = "test";
        MessageBusHandlerException exception = new MessageBusHandlerException(str);
        assertEquals(exception.getMessage(), str);
        throw exception;
    }

    @Test (expected = MessageBusHandlerException.class)
    public void checkCauseMethod()
            throws MessageBusHandlerException {
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        MessageBusHandlerException exception = new MessageBusHandlerException(cause);
        assertEquals(cause, exception.getCause());
        throw exception;
    }

    @Test (expected = MessageBusHandlerException.class)
    public void checkMessageAndCauseMethod()
            throws MessageBusHandlerException {
        String str = "test";
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        MessageBusHandlerException exception = new MessageBusHandlerException(str, cause);
        assertEquals(exception.getMessage(), str);
        assertEquals(exception.getCause(), cause);
        throw exception;
    }
}
