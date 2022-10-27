package info.smart_tools.smartactors.message_processing_interfaces.ichain_storage.exceptions;

import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.ChainNotFoundException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test for {@link ChainNotFoundException}.
 */
public class ChainModificationExceptionTest {
    @Test(expected = ChainModificationException.class)
    public void checkConstruction1()
            throws ChainModificationException {
        Exception cause = new Exception("cause");
        ChainModificationException exception = new ChainModificationException("test",cause);
        assertEquals(exception.getMessage(), "test");
        assertEquals(exception.getCause(), cause);
        throw exception;
    }

    @Test(expected = ChainModificationException.class)
    public void checkConstruction2()
            throws ChainModificationException {
        ChainModificationException exception = new ChainModificationException("test");
        assertEquals(exception.getMessage(), "test");
        throw exception;
    }

    @Test(expected = ChainModificationException.class)
    public void checkConstruction3()
            throws ChainModificationException {
        Exception cause = new Exception("cause");
        ChainModificationException exception = new ChainModificationException(cause);
        assertEquals(exception.getCause(), cause);
        throw exception;
    }
}
