package info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test for {@link MessageReceiveException}.
 */
public class MessageProcessorProcessExceptionTest {

    @Test (expected = MessageProcessorProcessException.class)
    public void checkMessageAndCauseMethod()
            throws MessageProcessorProcessException {
        String message = "test";
        String causeMessage = "cause message";
        Throwable cause = new Throwable(causeMessage);
        MessageProcessorProcessException exception = new MessageProcessorProcessException(message, cause);
        assertEquals(exception.getMessage(), message);
        assertEquals(exception.getCause(), cause);
        throw exception;
    }

    @Test (expected = MessageProcessorProcessException.class)
    public void checkMessageMethod()
            throws MessageProcessorProcessException {
        String message = "test";
        MessageProcessorProcessException exception = new MessageProcessorProcessException(message);
        assertEquals(exception.getMessage(), message);
        throw exception;
    }

    @Test (expected = MessageProcessorProcessException.class)
    public void checkCauseMethod()
            throws MessageProcessorProcessException {
        String causeMessage = "cause message";
        Throwable cause = new Throwable(causeMessage);
        MessageProcessorProcessException exception = new MessageProcessorProcessException(cause);
        assertEquals(exception.getCause(), cause);
        throw exception;
    }
}
