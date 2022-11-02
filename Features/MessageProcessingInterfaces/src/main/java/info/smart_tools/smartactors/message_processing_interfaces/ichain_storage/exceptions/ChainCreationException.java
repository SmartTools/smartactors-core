package info.smart_tools.smartactors.message_processing_interfaces.ichain_storage.exceptions;

import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.message_processing_interfaces.ichain_storage.IChainStorage;

/**
 * Exception thrown by {@link IChainStorage#register(Object, IObject)}
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
