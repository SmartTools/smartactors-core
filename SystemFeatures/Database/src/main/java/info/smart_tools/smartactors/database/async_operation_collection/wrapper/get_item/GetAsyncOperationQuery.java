package info.smart_tools.smartactors.database.async_operation_collection.wrapper.get_item;

import info.smart_tools.smartactors.database.database_storage.utils.CollectionName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;

import java.util.List;

/**
 * Wrapper for get async operation query
 */
public interface GetAsyncOperationQuery {
    /**
     * Setter
     * @param collectionName collection name object
     * @throws ChangeValueException if error during set is occurred
     */
    void setCollectionName(CollectionName collectionName) throws ChangeValueException;

    /**
     * Setter
     * @param token operation token
     * @throws ChangeValueException if error during set is occurred
     */
    void setToken(String token) throws ChangeValueException;

    /**
     * Getter
     * @return token
     * @throws ReadValueException if error during get is occurred
     */
    String getToken() throws ReadValueException;

    /**
     * @return wrapped IObject which was chosen
     * @throws ReadValueException if error during get is occurred
     */
    IObject wrapped() throws ReadValueException;

    /**
     * Getter
     * @return search result list
     * @throws ReadValueException if error during get is occurred
     */
    List<IObject> getSearchResult() throws ReadValueException;

    /**
     * Setter
     * @param pageNumber page number
     * @throws ChangeValueException if error during set is occurred
     */
    void setPageNumber(Integer pageNumber) throws ChangeValueException;

    /**
     * Setter
     * @param pageSize page size
     * @throws ChangeValueException if error during set is occurred
     */
    void setPageSize(Integer pageSize) throws ChangeValueException;

    /**
     * Setter
     * @param query search query object
     * @throws ChangeValueException if error during set is occurred
     */
    void setQuery(AsyncOperationTaskQuery query) throws ChangeValueException;
}
