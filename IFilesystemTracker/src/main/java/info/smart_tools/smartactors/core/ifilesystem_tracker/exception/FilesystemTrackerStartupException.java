package info.smart_tools.smartactors.core.ifilesystem_tracker.exception;

import info.smart_tools.smartactors.core.ifilesystem_tracker.IFilesystemTracker;

import java.io.File;

/**
 * Exception thrown by {@link IFilesystemTracker#start(File)}.
 */
public class FilesystemTrackerStartupException extends Exception {
    /**
     * Constructor with specific error message as argument
     * @param message specific error message
     */
    public FilesystemTrackerStartupException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */
    public FilesystemTrackerStartupException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with specific cause as argument
     * @param cause specific cause
     */
    public FilesystemTrackerStartupException(final Throwable cause) {
        super(cause);
    }
}
