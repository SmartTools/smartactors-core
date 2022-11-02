package info.smart_tools.smartactors.database.async_operation_collection.wrapper.update;

import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;

/**
 * Wrapper for async operation for update
 */
public interface UpdateItem {

    /**
     * Setter
     * @param isDone complete flag
     * @throws ChangeValueException if error during set is occurred
     */
    void setIsDone(Boolean isDone) throws ChangeValueException;
}
