package info.smart_tools.smartactors.async_operations.create_async_operation.wrapper;

import info.smart_tools.smartactors.async_operations.create_async_operation.CreateAsyncOperationActor;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;

import java.util.List;

/**
 * Wrapper for {@link CreateAsyncOperationActor} handler
 */
public interface CreateAsyncOperationMessage {

    /**
     * Getter
     * @return session identifier
     * @throws ReadValueException if error during get is occurred
     */
    IObject getOperationData() throws ReadValueException;

    /**
     * Getter
     * @return TTL for async operation
     * @throws ReadValueException if error during get is occurred
     */
    Integer getExpiredTime() throws ReadValueException;

    /**
     * Getter
     * @return Return sessionId of user
     * @throws ReadValueException if the value cannot be read by any reason
     */
    String getSessionId() throws ReadValueException;

    /**
     * Must set sessionId in target asyncData
     * @param sessionId Target sessionId
     * @throws ChangeValueException if the value cannot be changed by any reason
     */
    void setSessionIdInData(String sessionId) throws ChangeValueException;

    /**
     * Setter
     * @param token operation unique identifier
     * @throws ChangeValueException if error during set is occurred
     */
    void setAsyncOperationToken(String token) throws ChangeValueException;

    /**
     * Getter from list of tokens in session
     * @return list of tokens of async op's
     * @throws ReadValueException Throw when can't correct read value
     */
    List<String> getOperationTokens() throws ReadValueException;

    /**
     * Setter to list of tokens in session
     * @param operationTokens list of tokens of async op's
     * @throws ChangeValueException if error during set is occurred
     */
    void setOperationTokens(List<String> operationTokens) throws ChangeValueException;
}
