package info.smart_tools.smartactors.base.interfaces.serialization;

import info.smart_tools.smartactors.base.interfaces.serialization.exception.CacheDropException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CacheDropExceptionTest {

    @Test(expected = CacheDropException.class)
    public void checkMessageMethod()
            throws CacheDropException {
        String str = "test";
        CacheDropException exception = new CacheDropException(str);
        assertEquals(exception.getMessage(), str);
        throw exception;
    }

    @Test(expected = CacheDropException.class)
    public void checkCauseMethod()
            throws CacheDropException {
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        CacheDropException exception = new CacheDropException(cause);
        assertEquals(cause, exception.getCause());
        throw exception;
    }

    @Test (expected = CacheDropException.class)
    public void checkMessageAndCauseMethod()
            throws CacheDropException {
        String str = "test";
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        CacheDropException exception = new CacheDropException(str, cause);
        assertEquals(exception.getMessage(), str);
        assertEquals(exception.getCause(), cause);
        throw exception;
    }
}
