package info.smart_tools.smartactors.core.ichain_storage.exceptions;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test for {@link ChainCreationException}.
 */
public class ChainCreationExceptionTest {
    @Test(expected = ChainCreationException.class)
    public void checkMessageAndCauseMethod()
            throws ChainCreationException {
        String str = "test";
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        ChainCreationException exception = new ChainCreationException(str, cause);
        assertEquals(exception.getMessage(), str);
        assertEquals(exception.getCause(), cause);
        throw exception;
    }
}
