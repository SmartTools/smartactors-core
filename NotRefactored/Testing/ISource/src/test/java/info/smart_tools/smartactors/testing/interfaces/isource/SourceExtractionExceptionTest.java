package info.smart_tools.smartactors.testing.interfaces.isource;

import info.smart_tools.smartactors.testing.interfaces.isource.exception.SourceExtractionException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link SourceExtractionException}.
 */
public class SourceExtractionExceptionTest {

    @Test(expected = SourceExtractionException.class)
    public void checkMessageMethod()
            throws SourceExtractionException {
        String str = "test";
        SourceExtractionException exception = new SourceExtractionException(str);
        assertEquals(exception.getMessage(), str);
        throw exception;
    }

    @Test (expected = SourceExtractionException.class)
    public void checkCauseMethod()
            throws SourceExtractionException {
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        SourceExtractionException exception = new SourceExtractionException(cause);
        assertEquals(cause, exception.getCause());
        throw exception;
    }

    @Test (expected = SourceExtractionException.class)
    public void checkMessageAndCauseMethod()
            throws SourceExtractionException {
        String str = "test";
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        SourceExtractionException exception = new SourceExtractionException(str, cause);
        assertEquals(exception.getMessage(), str);
        assertEquals(exception.getCause(), cause);
        throw exception;
    }
}
