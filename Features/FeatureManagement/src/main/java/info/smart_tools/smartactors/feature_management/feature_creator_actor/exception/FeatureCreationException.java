package info.smart_tools.smartactors.feature_management.feature_creator_actor.exception;

/**
 * Exception for {@link info.smart_tools.smartactors.feature_management.feature_creator_actor.FeaturesCreatorActor}
 */
public class FeatureCreationException extends Exception {
    /**
     * Constructor with specific error message as argument
     * @param message specific error message
     */
    public FeatureCreationException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */
    public FeatureCreationException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with specific cause as argument
     * @param cause specific cause
     */
    public FeatureCreationException(final Throwable cause) {
        super(cause);
    }
}
