package info.smart_tools.smartactors.database.cached_collection.wrapper.upsert;

import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;

import java.time.LocalDateTime;

/**
 * Using in @UpsertIntoCachedCollectionQuery
 */
public interface UpsertItem {

    /**
     * @return ID of object
     * @throws ReadValueException Calling when try read value of variable
     */
    String getId() throws ReadValueException;

    /**
     * @return isActive flag of object
     * @throws ReadValueException Calling when try read value of variable
     */
    Boolean isActive() throws ReadValueException;

    /**
     * @param isActive Flag of active of object
     * @throws ChangeValueException Calling when try change value of variable
     */
    void setIsActive(Boolean isActive) throws ChangeValueException;

    /**
     * @return Key value
     * @throws ReadValueException Calling when try read value of variable
     */
    String getKey() throws ReadValueException;

    /**
     * @return DateTime of object
     * @throws ReadValueException Calling when try read value of variable
     */
    LocalDateTime getStartDateTime() throws ReadValueException;

    /**
     * @param startDateTime DateTime in format as DateTimeInstance.now()
     * @throws ChangeValueException Calling when try change value of variable
     */
    void setStartDateTime(LocalDateTime startDateTime) throws ChangeValueException;

    //NOTE:: this is proto of method instead of extractWrapped() from IObjectWrapper
    /**
     * @return This is proto of method instead of extractWrapped() from IObjectWrapper
     * @throws ReadValueException Calling when try read value of variable
     * @throws ChangeValueException Calling when try change value of variable
     */
    IObject wrapped() throws ReadValueException, ChangeValueException;
}
