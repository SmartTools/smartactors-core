package info.smart_tools.smartactors.database.cached_collection.wrapper.delete;

import info.smart_tools.smartactors.database.database_storage.utils.CollectionName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;

/**
 * Using in @DeleteFromCachedCollectionTask
 */
public interface DeleteFromCachedCollectionQuery {

    /**
     * @param collectionName Target collection name
     * @throws ChangeValueException Calling when try change value of variable
     */
    void setCollectionName(CollectionName collectionName) throws ChangeValueException;

    /**
     * @param deleteItem Set the item for deleting
     * @throws ChangeValueException Calling when try change value of variable
     */
    void setDeleteItem(DeleteItem deleteItem) throws ChangeValueException;

    /**
     * @return Item for deleting
     * @throws ReadValueException Calling when try read value of variable
     */
    DeleteItem getDeleteItem() throws ReadValueException;

    //NOTE:: this is proto of method instead of extractWrapped() from IObjectWrapper
    /**
     *
     * @return This is proto of method instead of extractWrapped() from IObjectWrapper
     * @throws ReadValueException Calling when try read value of variable
     * @throws ChangeValueException Calling when try change value of variable
     */
    IObject wrapped() throws ReadValueException, ChangeValueException;
}
