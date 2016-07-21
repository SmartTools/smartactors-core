package info.smart_tools.smartactors.core.ichain_storage.exceptions;

/**
 * Exception thrown by {@link info.smart_tools.smartactors.core.ichain_storage.IChainStorage#resolve(Object)} when there
 * is no chain associated with given identifier.
 */
public class ChainNotFoundException extends Exception {
    /**
     * Constructor with specific error message as argument
     * @param message specific error message
     */
    public ChainNotFoundException(final String message) {
        super(message);
    }
}
