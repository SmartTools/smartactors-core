package info.smart_tools.smartactors.core.async_operation_collection.wrapper.create_item;

import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;

/**
 * Wrapper for asynchronous operation from collection
 */
public interface AsyncDocument {

    /**
     * Setter
     * @param token unique operation identifier
     * @throws ChangeValueException if error during set is occurred
     */
    void setToken(String token) throws ChangeValueException;

    /**
     * Setter
     * @param data IObject with custom data for specific operation
     * @throws ChangeValueException if error during set is occurred
     */
    void setAsyncData(IObject data) throws ChangeValueException;

    /**
     * Setter
     * @param time TTL for operation
     * @throws ChangeValueException if error during set is occurred
     */
    void setExpiredTime(String time) throws ChangeValueException;

    /**
     * Setter
     * @param flag mark operation as completed
     * @throws ChangeValueException if error during set is occurred
     */
    void setDoneFlag(Boolean flag) throws ChangeValueException;

    /**
     * Getter
     * TODO:: use instead this smth like getInitObject from IObjectWrapper
     * @return init iobject
     * @throws ReadValueException read error
     */
    IObject getIObject() throws ReadValueException;
}
