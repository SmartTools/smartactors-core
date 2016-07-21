package info.smart_tools.smartactors.core.async_operation_collection.wrapper.get_item;

import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;

/**
 * Wrapper for read task-facade query
 */
public interface AsyncOperationTaskQuery {

    /**
     * Sets token
     * @param query equals condition object
     * @throws ChangeValueException change value ex
     */
    void setToken(final EQMessage query) throws ChangeValueException;
}
