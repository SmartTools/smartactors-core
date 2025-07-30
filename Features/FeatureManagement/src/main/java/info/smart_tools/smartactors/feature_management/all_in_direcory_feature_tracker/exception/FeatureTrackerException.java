package info.smart_tools.smartactors.feature_management.all_in_direcory_feature_tracker.exception;


/**
 * Exception for {@link info.smart_tools.smartactors.feature_management.all_in_direcory_feature_tracker.AllInDirectoryFeatureTracker}
 */
public class FeatureTrackerException extends Exception {
    /**
     * Constructor with specific error message as argument
     * @param message specific error message
     */
    public FeatureTrackerException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */
    public FeatureTrackerException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with specific cause as argument
     * @param cause specific cause
     */
    public FeatureTrackerException(final Throwable cause) {
        super(cause);
    }
}
