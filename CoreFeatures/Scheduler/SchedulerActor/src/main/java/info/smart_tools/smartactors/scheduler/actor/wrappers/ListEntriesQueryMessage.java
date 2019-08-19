package info.smart_tools.smartactors.scheduler.actor.wrappers;

import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;

import java.util.List;

/**
 * Wrapper for a message representing a query for list of all scheduler entries.
 */
public interface ListEntriesQueryMessage {
    /**
     * Set query result.
     *
     * @param entries    list of state objects of all entries
     * @throws ChangeValueException if error occurs writing value to the message
     */
    void setEntries(List<IObject> entries) throws ChangeValueException;
}
