package info.smart_tools.smartactors.actors.get_async_operation.wrapper;

import info.smart_tools.smartactors.actors.get_async_operation.GetAsyncOperationActor;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;

/**
 * Wrapper for {@link GetAsyncOperationActor} message
 */
public interface GetAsyncOperationMessage {

    /**
     * Getter
     * @return unique identifier of operation
     * @throws ReadValueException if error during get is occurred
     */
    String getToken() throws ReadValueException;

    /**
     * Setter
     * @param asyncOperation async operation object
     * @throws ChangeValueException if error during set is occurred
     */
    void setAsyncOperation(IObject asyncOperation) throws ChangeValueException;
}
