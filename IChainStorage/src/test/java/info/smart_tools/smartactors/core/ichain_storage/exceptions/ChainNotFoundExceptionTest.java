package info.smart_tools.smartactors.core.ichain_storage.exceptions;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test for {@link ChainNotFoundException}.
 */
public class ChainNotFoundExceptionTest {
    @Test(expected = ChainNotFoundException.class)
    public void checkMessageMethod()
            throws ChainNotFoundException {
        String str = "test";
        ChainNotFoundException exception = new ChainNotFoundException(str);
        assertEquals(exception.getMessage(), str);
        throw exception;
    }
}