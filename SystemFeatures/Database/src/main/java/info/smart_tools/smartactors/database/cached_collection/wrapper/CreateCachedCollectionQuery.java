package info.smart_tools.smartactors.database.cached_collection.wrapper;

import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;

import java.util.Map;

/**
 * Query which will bew created for @CreateCachedCollectionTask
 */
public interface CreateCachedCollectionQuery {

    /**
     * @return Key for this collection
     * @throws ReadValueException Calling when try read value of variable
     */
    String getKey() throws ReadValueException;

    /**
     * @param indexes Map of indexes for creating collection
     * @throws ChangeValueException Calling when try change value of variable
     */
    void setIndexes(Map<String, String> indexes) throws ChangeValueException;

    //NOTE:: this is proto of method instead of extractWrapped() from IObjectWrapper
    /**
     *
     * @return This is proto of method instead of extractWrapped() from IObjectWrapper
     * @throws ReadValueException Calling when try read value of variable
     * @throws ChangeValueException Calling when try change value of variable
     */
    IObject wrapped() throws ReadValueException, ChangeValueException;
}
