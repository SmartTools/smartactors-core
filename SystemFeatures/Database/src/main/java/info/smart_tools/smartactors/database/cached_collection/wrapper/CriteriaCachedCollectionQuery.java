package info.smart_tools.smartactors.database.cached_collection.wrapper;

import info.smart_tools.smartactors.database.cached_collection.wrapper.get_item.DateToMessage;
import info.smart_tools.smartactors.database.cached_collection.wrapper.get_item.EQMessage;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;

/**
 * Need for create criteries for searching
 */
public interface CriteriaCachedCollectionQuery {
    /**
     * @param query Await @DateToMessage
     * @throws ChangeValueException Calling when try change value of variable
     */
    void setStartDateTime(DateToMessage query) throws ChangeValueException;

    /**
     * @param query Await @EQMessage with boolean isActive flag
     * @throws ChangeValueException Calling when try change value of variable
     */
    void setIsActive(EQMessage query) throws ChangeValueException;

    /**
     * @param query Await @EQMessage with String isActive value
     * @throws ChangeValueException Calling when try change value of variable
     */
    void setKey(EQMessage query) throws ChangeValueException; // TODO: write custom name for field

    //NOTE:: this is proto of method instead of extractWrapped() from IObjectWrapper
    /**
     *
     * @return This is proto of method instead of extractWrapped() from IObjectWrapper
     * @throws ReadValueException Calling when try read value of variable
     * @throws ChangeValueException Calling when try change value of variable
     */
    IObject wrapped() throws ReadValueException, ChangeValueException;
}
