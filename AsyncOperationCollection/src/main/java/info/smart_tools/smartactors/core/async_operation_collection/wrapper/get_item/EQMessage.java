package info.smart_tools.smartactors.core.async_operation_collection.wrapper.get_item;

import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;

/**
 * Wrapper for equals condition
 */
public interface EQMessage {

    /**
     * Sets value to compare
     * @param val value to compare
     * @throws ChangeValueException if error during set is occurred
     */
    void setEq(String val) throws ChangeValueException;
}
