package info.smart_tools.smartactors.core.cached_collection.wrapper.get_item;

import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;

/**
 * Using in criteria messages for $dateTo field
 */
public interface DateToMessage {
    /**
     * @param dateTo String with date as value (Await format as return DateTimeInstance.now())
     * @throws ChangeValueException Calling when try change value of variable
     */
    void setDateTo(String dateTo) throws ChangeValueException; // TODO: write custom name for field
}
