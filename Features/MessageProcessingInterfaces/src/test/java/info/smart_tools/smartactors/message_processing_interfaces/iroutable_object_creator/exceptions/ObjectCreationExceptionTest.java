package info.smart_tools.smartactors.message_processing_interfaces.iroutable_object_creator.exceptions;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test for {@link ObjectCreationException}.
 */
public class ObjectCreationExceptionTest {
    @Test(expected = ObjectCreationException.class)
    public void checkMessageMethod()
            throws ObjectCreationException {
        String str = "test";
        ObjectCreationException exception = new ObjectCreationException(str);
        assertEquals(exception.getMessage(), str);
        throw exception;
    }

    @Test(expected = ObjectCreationException.class)
    public void checkCauseMethod()
            throws ObjectCreationException {
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        ObjectCreationException exception = new ObjectCreationException(cause);
        assertEquals(cause, exception.getCause());
        throw exception;
    }

    @Test (expected = ObjectCreationException.class)
    public void checkMessageAndCauseMethod()
            throws ObjectCreationException {
        String str = "test";
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        ObjectCreationException exception = new ObjectCreationException(str, cause);
        assertEquals(exception.getMessage(), str);
        assertEquals(exception.getCause(), cause);
        throw exception;
    }
}
