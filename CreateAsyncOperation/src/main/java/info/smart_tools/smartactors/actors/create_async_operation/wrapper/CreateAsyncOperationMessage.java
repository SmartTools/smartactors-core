package info.smart_tools.smartactors.actors.create_async_operation.wrapper;

import info.smart_tools.smartactors.actors.create_async_operation.CreateAsyncOperationActor;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;

/**
 * Wrapper for {@link CreateAsyncOperationActor} handler
 */
public interface CreateAsyncOperationMessage {

    /**
     * Getter
     * @return session identifier
     * @throws ReadValueException if error during get is occurred
     */
    String getSessionId() throws ReadValueException;

    /**
     * Getter
     * @return TTL for async operation
     * @throws ReadValueException if error during get is occurred
     */
    Long getExpiredTime() throws ReadValueException;

    /**
     * Setter
     * @param token operation unique identifier
     * @throws ChangeValueException if error during set is occurred
     */
    void setAsyncOperationToken(String token) throws ChangeValueException;
}
