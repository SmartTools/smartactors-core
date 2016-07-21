package info.smart_tools.smartactors.core.examples.actor;

/**
 * An exception throws by {@link HelloActor}.
 */
public class HelloActorException extends Exception {

    /**
     * Creates the exception.
     * @param cause the error cause
     */
    public HelloActorException(final Exception cause) {
        super(cause);
    }

}
