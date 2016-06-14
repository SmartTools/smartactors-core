package info.smart_tools.smartactors.core.ifilesystem_tracker;

import info.smart_tools.smartactors.core.ifilesystem_tracker.exception.FilesystemTrackerStartupException;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for {@link FilesystemTrackerStartupException}.
 */
public class FilesystemTrackerStartupExceptionTest {
    @Test(expected = FilesystemTrackerStartupException.class)
    public void checkMessageMethod()
            throws FilesystemTrackerStartupException {
        String str = "test";
        FilesystemTrackerStartupException exception = new FilesystemTrackerStartupException(str);
        assertEquals(exception.getMessage(), str);
        throw exception;
    }

    @Test(expected = FilesystemTrackerStartupException.class)
    public void checkCauseMethod()
            throws FilesystemTrackerStartupException {
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        FilesystemTrackerStartupException exception = new FilesystemTrackerStartupException(cause);
        assertEquals(cause, exception.getCause());
        throw exception;
    }

    @Test (expected = FilesystemTrackerStartupException.class)
    public void checkMessageAndCauseMethod()
            throws FilesystemTrackerStartupException {
        String str = "test";
        String internalMessage = "Internal message";
        Throwable cause = new Throwable(internalMessage);
        FilesystemTrackerStartupException exception = new FilesystemTrackerStartupException(str, cause);
        assertEquals(exception.getMessage(), str);
        assertEquals(exception.getCause(), cause);
        throw exception;
    }
}
