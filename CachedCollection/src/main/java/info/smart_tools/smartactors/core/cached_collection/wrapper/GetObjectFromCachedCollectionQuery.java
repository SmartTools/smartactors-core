package info.smart_tools.smartactors.core.cached_collection.wrapper;

import info.smart_tools.smartactors.core.db_storage.utils.CollectionName;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;

import java.util.stream.Stream;

/**
 * Using in @GetObjectFromCachedCollectionTask
 */
public interface GetObjectFromCachedCollectionQuery {
    /**
     * @param number Number of page in search results
     * @throws ChangeValueException Calling when try change value of variable
     */
    void setPageNumber(Integer number) throws ChangeValueException;

    /**
     * @param criteria Criteria of searching
     * @throws ChangeValueException Calling when try change value of variable
     */
    void setCriteria(Object criteria) throws ChangeValueException;

    /**
     * @param size Size of page in search results
     * @throws ChangeValueException Calling when try change value of variable
     */
    void setPageSize(Integer size) throws ChangeValueException;

    /**
     * @return Key value
     * @throws ReadValueException Calling when try read value of variable
     */
    String getKey() throws ReadValueException; // TODO: write custom name for field

    /**
     * @param collectionName Target collection name
     * @throws ChangeValueException Calling when try change value of variable
     */
    void setCollectionName(CollectionName collectionName) throws ChangeValueException;

    /**
     * @param key Target key in collection
     * @throws ChangeValueException Calling when try change value of variable
     */
    void setKey(String key) throws ChangeValueException;

    /**
     * @return Stream of IObjects which is results of searching
     * @throws ReadValueException Calling when try read value of variable
     */
    Stream<IObject> getSearchResult() throws ReadValueException;

    //NOTE:: this is proto of method instead of extractWrapped() from IObjectWrapper
    /**
     * @return This is proto of method instead of extractWrapped() from IObjectWrapper
     * @throws ReadValueException Calling when try read value of variable
     * @throws ChangeValueException Calling when try change value of variable
     */
    IObject wrapped() throws ReadValueException, ChangeValueException;
}
