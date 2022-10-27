package info.smart_tools.smartactors.message_processing_interfaces.ichain_storage.exceptions;

import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.ChainNotFoundException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test for {@link ChainNotFoundException}.
 */
public class ChainNotFoundExceptionTest {
    @Test(expected = ChainNotFoundException.class)
    public void checkConstruction()
            throws ChainNotFoundException {
        ChainNotFoundException exception = new ChainNotFoundException("test");
        assertEquals(exception.getMessage(), "Chain 'test' not found.");
        throw exception;
    }
}
