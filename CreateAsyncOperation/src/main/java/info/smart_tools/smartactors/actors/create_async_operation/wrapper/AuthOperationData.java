package info.smart_tools.smartactors.actors.create_async_operation.wrapper;

import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;

/**
 * Wrapper for data object of authentication operation
 */
public interface AuthOperationData {

    /**
     * Setter
     * @param sessionId session identifier
     * @throws ChangeValueException if error during set is occurred
     */
    void setSessionId(String sessionId) throws ChangeValueException;
}
