package info.smart_tools.smartactors.core.ichain_storage.exceptions;

import info.smart_tools.smartactors.core.iobject.IObject;

/**
 * Exception thrown by {@link info.smart_tools.smartactors.core.ichain_storage.IChainStorage#register(Object, IObject)}
 * if any error occurs during creation of a chain.
 */
public class ChainCreationException extends Exception {
    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */
    public ChainCreationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
