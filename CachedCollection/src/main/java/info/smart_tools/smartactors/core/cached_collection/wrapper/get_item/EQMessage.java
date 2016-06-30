package info.smart_tools.smartactors.core.cached_collection.wrapper.get_item;

import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;

/**
 * Using for $eq field in criteria messages
 */
public interface EQMessage {
    /**
     * @param val EQ value
     * @throws ChangeValueException Calling when try change value of variable
     */
    void setEq(String val) throws ChangeValueException; // TODO: write custom name for field
}
