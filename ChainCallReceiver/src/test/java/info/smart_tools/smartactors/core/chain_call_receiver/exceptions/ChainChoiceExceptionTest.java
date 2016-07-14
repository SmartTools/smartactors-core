package info.smart_tools.smartactors.core.chain_call_receiver.exceptions;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test for {@link ChainChoiceException}.
 */
public class ChainChoiceExceptionTest {
    @Test(expected = ChainChoiceException.class)
    public void checkMessageMethod()
            throws ChainChoiceException {
        String str = "test";
        ChainChoiceException exception = new ChainChoiceException(str);
        assertEquals(exception.getMessage(), str);
        throw exception;
    }

    @Test (expected = ChainChoiceException.class)
    public void checkMessageAndCauseMethod()
            throws ChainChoiceException {
        String str = "test";
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        ChainChoiceException exception = new ChainChoiceException(str, cause);
        assertEquals(exception.getMessage(), str);
        assertEquals(exception.getCause(), cause);
        throw exception;
    }
}
