package info.smart_tools.smartactors.core.iresource_source.exceptions;

import info.smart_tools.smartactors.core.iaction.IPoorAction;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iresource_source.IResourceSource;

/**
 * Exception thrown by methods of implementations of {@link IResourceSource} when client tries to access resource that
 * is not available but will become available later (the client may use {@link IResourceSource#onAvailable(IPoorAction)}
 * to wait for resource).
 */
public class OutOfResourceException extends Exception {
    private IResourceSource source;

    /**
     * The constructor.
     *
     * @param source {@link IResourceSource} that caused this exception
     * @throws InvalidArgumentException if source is {@code null}.
     */
    public OutOfResourceException(final IResourceSource source)
            throws InvalidArgumentException {
        if (null == source) {
            throw new InvalidArgumentException("Resource source should not be null.");
        }

        this.source = source;
    }

    /**
     * Get {@link IResourceSource} that caused this exception.
     *
     * @return the {@link IResourceSource} that caused this exception.
     */
    public IResourceSource getSource() {
        return this.source;
    }
}
