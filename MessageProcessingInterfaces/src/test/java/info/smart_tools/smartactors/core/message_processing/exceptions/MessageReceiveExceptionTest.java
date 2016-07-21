package info.smart_tools.smartactors.core.message_processing.exceptions;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test for {@link MessageReceiveException}.
 */
public class MessageReceiveExceptionTest {
    @Test(expected = MessageReceiveException.class)
    public void checkMessageMethod()
            throws MessageReceiveException {
        String str = "test";
        MessageReceiveException exception = new MessageReceiveException(str);
        assertEquals(exception.getMessage(), str);
        throw exception;
    }

    @Test(expected = MessageReceiveException.class)
    public void checkCauseMethod()
            throws MessageReceiveException {
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        MessageReceiveException exception = new MessageReceiveException(cause);
        assertEquals(cause, exception.getCause());
        throw exception;
    }

    @Test (expected = MessageReceiveException.class)
    public void checkMessageAndCauseMethod()
            throws MessageReceiveException {
        String str = "test";
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        MessageReceiveException exception = new MessageReceiveException(str, cause);
        assertEquals(exception.getMessage(), str);
        assertEquals(exception.getCause(), cause);
        throw exception;
    }
}
