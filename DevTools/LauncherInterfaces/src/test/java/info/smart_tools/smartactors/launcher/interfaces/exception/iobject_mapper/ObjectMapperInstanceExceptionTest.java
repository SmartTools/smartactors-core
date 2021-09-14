package info.smart_tools.smartactors.launcher.interfaces.exception.iobject_mapper;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link ObjectMapperInstanceException}
 */
public class ObjectMapperInstanceExceptionTest {
    @Test(expected = ObjectMapperInstanceException.class)
    public void checkMessageMethod()
            throws ObjectMapperInstanceException {
        String str = "test";
        ObjectMapperInstanceException exception = new ObjectMapperInstanceException(str);
        assertEquals(exception.getMessage(), str);
        throw exception;
    }

    @Test(expected = ObjectMapperInstanceException.class)
    public void checkCauseMethod()
            throws ObjectMapperInstanceException {
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        ObjectMapperInstanceException exception = new ObjectMapperInstanceException(cause);
        assertEquals(cause, exception.getCause());
        throw exception;
    }

    @Test (expected = ObjectMapperInstanceException.class)
    public void checkMessageAndCauseMethod()
            throws ObjectMapperInstanceException {
        String str = "test";
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        ObjectMapperInstanceException exception = new ObjectMapperInstanceException(str, cause);
        assertEquals(exception.getMessage(), str);
        assertEquals(exception.getCause(), cause);
        throw exception;
    }
}
