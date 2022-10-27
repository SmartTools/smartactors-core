package info.smart_tools.smartactors.feature_management.directory_watcher_actor.exception;


/**
 * Exception for {@link info.smart_tools.smartactors.feature_management.directory_watcher_actor.RuntimeDirectoryFeatureTracker}
 */
public class WatchingServiceException extends Exception {
    /**
     * Constructor with specific error message as argument
     * @param message specific error message
     */
    public WatchingServiceException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */
    public WatchingServiceException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with specific cause as argument
     * @param cause specific cause
     */
    public WatchingServiceException(final Throwable cause) {
        super(cause);
    }
}
