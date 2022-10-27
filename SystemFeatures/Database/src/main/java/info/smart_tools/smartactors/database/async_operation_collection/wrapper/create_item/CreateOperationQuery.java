package info.smart_tools.smartactors.database.async_operation_collection.wrapper.create_item;

import info.smart_tools.smartactors.database.database_storage.utils.CollectionName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;

/**
 * Wrapper for query for create asynchronous operation
 */
public interface CreateOperationQuery {

    /**
     * Setter
     * @param document operation to insert
     * @throws ChangeValueException if error during set is occurred
     */
    void setDocument(IObject document) throws ChangeValueException;

    /**
     * Setter
     * @param collectionName wrapped collection name
     * @throws ChangeValueException if error during set is occurred
     */
    void setCollectionName(CollectionName collectionName) throws ChangeValueException;

    /**
     * Getter
     * TODO:: use instead this smth like getInitObject from IObjectWrapper
     * @return init object
     * @throws ReadValueException if error during get is occurred
     */
    IObject getIObject() throws ReadValueException;

    /**
     * Getter
     * @return async data
     * @throws ReadValueException if error during get is occurred
     */
    IObject getAsyncData() throws ReadValueException;

    /**
     * Getter
     * @return token
     * @throws ReadValueException if error during get is occurred
     */
    String getToken() throws ReadValueException;

    /**
     * Getter
     * @return TTL
     * @throws ReadValueException if error during get is occurred
     */
    String getExpiredTime() throws ReadValueException;

    /**
     * Setter
     * @param asyncData IObject with custom data for specific operation
     * @throws ChangeValueException if error during set is occurred
     */
    void setAsyncData(IObject asyncData) throws ChangeValueException;

    /**
     * Setter
     * @param token unique operation identifier
     * @throws ChangeValueException if error during set is occurred
     */
    void setToken(String token) throws ChangeValueException;

    /**
     * Setter
     * @param expiredTime TTL for operation
     * @throws ChangeValueException if error during set is occurred
     */
    void setExpiredTime(String expiredTime) throws ChangeValueException;
}
