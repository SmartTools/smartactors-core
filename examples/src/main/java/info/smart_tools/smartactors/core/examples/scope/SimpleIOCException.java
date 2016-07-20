package info.smart_tools.smartactors.core.examples.scope;

/**
 *  Just an exception thrown by our IoC.
 */
public class SimpleIOCException extends Throwable {

    /**
     * Creates the exception with the specified cause.
     * @param e the cause of the exception
     */
    public SimpleIOCException(final Throwable e) {
        super(e);
    }

}
