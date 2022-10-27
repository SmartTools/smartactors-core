package info.smart_tools.smartactors.remote_management.feature_load_starter_actor.exception;

public class FeatureLoadStarterException extends Exception {

    public FeatureLoadStarterException(final String message) {
        super(message);
    }

    public FeatureLoadStarterException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public FeatureLoadStarterException(final Throwable cause) {
        super(cause);
    }
}
