package info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test for {@link MessageReceiveException}.
 */
public class AsynchronousOperationExceptionTest {

    @Test (expected = AsynchronousOperationException.class)
    public void checkMessageAndCauseMethod()
            throws AsynchronousOperationException {
        String message = "test";
        String causeMessage = "cause message";
        Throwable cause = new Throwable(causeMessage);
        AsynchronousOperationException exception = new AsynchronousOperationException(message, cause);
        assertEquals(exception.getMessage(), message);
        assertEquals(exception.getCause(), cause);
        throw exception;
    }

    @Test (expected = AsynchronousOperationException.class)
    public void checkMessageMethod()
            throws AsynchronousOperationException {
        String message = "test";
        AsynchronousOperationException exception = new AsynchronousOperationException(message);
        assertEquals(exception.getMessage(), message);
        throw exception;
    }
}
