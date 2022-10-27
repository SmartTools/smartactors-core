package info.smart_tools.smartactors.scheduler.actor.wrappers;

import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;

/**
 * Wrapper for a message containing a query for creation of a new scheduler entry.
 */
public interface SetEntryIdMessage extends AddEntryQueryMessage {
    /**
     * Set identifier of the entry to message.
     * @param id identifier of the entry
     * @throws ChangeValueException when something occurred.
     */
    void setEntryId(String id) throws ChangeValueException;
}
