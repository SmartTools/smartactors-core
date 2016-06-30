package info.smart_tools.smartactors.itransformation_rule.exception;

import info.smart_tools.smartactors.core.itransformation_rule.exception.TransformException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for TransformException
 */
public class TransformExceptionTest {
    @Test(expected = TransformException.class)
    public void checkMessageMethod() throws TransformException {
        String str = "test";
        TransformException exception = new TransformException(str);
        assertEquals(exception.getMessage(), str);
        throw exception;
    }

    @Test(expected = TransformException.class)
    public void checkCauseMethod()
            throws TransformException {
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        TransformException exception = new TransformException(cause);
        assertEquals(cause, exception.getCause());
        throw exception;
    }

    @Test(expected = TransformException.class)
    public void checkMessageAndCauseMethod()
            throws TransformException {
        String str = "test";
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        TransformException exception = new TransformException(str, cause);
        assertEquals(exception.getMessage(), str);
        assertEquals(exception.getCause(), cause);
        throw exception;
    }
}
