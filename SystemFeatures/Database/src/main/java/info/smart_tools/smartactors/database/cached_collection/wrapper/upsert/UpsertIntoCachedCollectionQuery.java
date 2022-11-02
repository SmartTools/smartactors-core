package info.smart_tools.smartactors.database.cached_collection.wrapper.upsert;

import info.smart_tools.smartactors.database.database_storage.utils.CollectionName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;

/**
 * Wrapper for @UpsertIntoCachedCollectionTask
 */
public interface UpsertIntoCachedCollectionQuery {

    /**
     * @param collectionName Target collection name
     * @throws ChangeValueException Calling when try change value of variable
     */
    void setCollectionName(CollectionName collectionName) throws ChangeValueException;

    /**
     * @param upsertItem Item for upserting
     * @throws ChangeValueException Calling when try change value of variable
     */
    void setUpsertItem(UpsertItem upsertItem) throws ChangeValueException;

    /**
     * @return Upserting item
     * @throws ReadValueException Calling when try read value of variable
     */
    UpsertItem getUpsertItem() throws ReadValueException;

    //NOTE:: this is proto of method instead of extractWrapped() from IObjectWrapper
    /**
     * @return This is proto of method instead of extractWrapped() from IObjectWrapper
     * @throws ReadValueException Calling when try read value of variable
     * @throws ChangeValueException Calling when try change value of variable
     */
    IObject wrapped() throws ReadValueException, ChangeValueException;
}
