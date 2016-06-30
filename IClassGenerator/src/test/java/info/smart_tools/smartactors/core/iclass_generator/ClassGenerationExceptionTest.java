package info.smart_tools.smartactors.core.iclass_generator;

import info.smart_tools.smartactors.core.iclass_generator.exception.ClassGenerationException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for ClassGenerationException
 */
public class ClassGenerationExceptionTest {

    @Test (expected = ClassGenerationException.class)
    public void checkMessageMethod()
            throws ClassGenerationException {
        String str = "test";
        ClassGenerationException exception = new ClassGenerationException(str);
        assertEquals(exception.getMessage(), str);
        throw exception;
    }

    @Test (expected = ClassGenerationException.class)
    public void checkCauseMethod()
            throws ClassGenerationException {
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        ClassGenerationException exception = new ClassGenerationException(cause);
        assertEquals(cause, exception.getCause());
        throw exception;
    }

    @Test (expected = ClassGenerationException.class)
    public void checkMessageAndCauseMethod()
            throws ClassGenerationException {
        String str = "test";
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        ClassGenerationException exception = new ClassGenerationException(str, cause);
        assertEquals(exception.getMessage(), str);
        assertEquals(exception.getCause(), cause);
        throw exception;
    }
}
