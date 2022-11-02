package info.smart_tools.smartactors.database.async_operation_collection.wrapper.update;

import info.smart_tools.smartactors.database.database_storage.utils.CollectionName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;

/**
 * Wrapper for complete async operation query
 */
public interface UpdateAsyncOperationQuery {

    /**
     * Setter
     * @param collectionName collection name object
     * @throws ChangeValueException if error during set is occurred
     */
    void setCollectionName(CollectionName collectionName) throws ChangeValueException;

    /**
     * Setter
     * @param updateItem async operation
     * @throws ChangeValueException if error during set is occurred
     */
    void setUpdateItem(IObject updateItem) throws ChangeValueException;

    /**
     * Getter
     * @return async operation for update
     * @throws ReadValueException if error during get is occurred
     */
    UpdateItem getUpdateItem() throws ReadValueException;
}
