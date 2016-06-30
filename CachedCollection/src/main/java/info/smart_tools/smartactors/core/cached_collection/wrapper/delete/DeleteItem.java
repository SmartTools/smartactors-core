package info.smart_tools.smartactors.core.cached_collection.wrapper.delete;

import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;

/**
 * Item for deleting which using in @DeleteFromCachedCollectionQuery
 */
public interface DeleteItem {

    /**
     * @return Id of document
     * @throws ReadValueException Calling when try read value of variable
     */
    String getId() throws ReadValueException;

    /**
     * @return Key value
     * @throws ReadValueException Calling when try read value of variable
     */
    String getKey() throws ReadValueException;

    /**
     * @param isActive Value of active flag
     * @throws ChangeValueException Calling when try change value of variable
     */
    void setIsActive(Boolean isActive) throws ChangeValueException;
}
