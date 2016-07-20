package info.smart_tools.smartactors.core.cached_collection;

import info.smart_tools.smartactors.core.cached_collection.exception.DeleteCacheItemException;
import info.smart_tools.smartactors.core.cached_collection.exception.GetCacheItemException;
import info.smart_tools.smartactors.core.cached_collection.exception.UpsertCacheItemException;
import info.smart_tools.smartactors.core.iobject.IObject;

import java.util.List;

/**
 * Stores objects in memory by some key and may operate with these objects into DB
 * Current collection contains documents with next fields:
 * "<key>": "keyValue" (value of <key> should be defined as a parameter of collection)
 * "startDateTime": "date and time of the beginning of activity of the document"
 * "isActive" "true\false flag of activity of the document"
 */
public interface ICachedCollection {

    /**
     * Reads active items by some key
     * @param key for cache
     * @return list with found objects
     * @throws GetCacheItemException Throw when Collection can't get items for key
     */
    List<IObject> getItems(String key) throws GetCacheItemException;

    /**
     * Deletes object from memory and set active flag to false for object into DB
     * @param query document for delete
     * @throws DeleteCacheItemException Throw when collection can't delete objects with @query
     */
    void delete(IObject query) throws DeleteCacheItemException;

    /**
     * Add or update object in memory and DB
     * @param query document for upsert
     * @throws UpsertCacheItemException Throw when collection can't update/insert objects in query
     */
    void upsert(IObject query) throws UpsertCacheItemException;
}