package info.smart_tools.smartactors.database.async_operation_collection.wrapper.delete;

import info.smart_tools.smartactors.database.database_storage.utils.CollectionName;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;

import java.util.List;

/**
 * Wrapper for delete async operation query
 */
public interface DeleteAsyncOperationQuery {

    /**
     * Setter
     * @param collectionName collection name object
     * @throws ChangeValueException if error during set is occurred
     */
    void setCollectionName(CollectionName collectionName) throws ChangeValueException;

    /**
     *  Setter
     * @param documentIds ids of operations for delete
     * @throws ChangeValueException if error during set is occurred
     */
    void setDocumentIds(List<Long> documentIds) throws ChangeValueException;
}
