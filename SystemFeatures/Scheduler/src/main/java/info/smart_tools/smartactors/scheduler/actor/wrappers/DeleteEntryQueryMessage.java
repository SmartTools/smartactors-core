package info.smart_tools.smartactors.scheduler.actor.wrappers;

import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;

/**
 * Wrapper for a message containing query for deletion (cancellation) of a scheduler entry.
 */
public interface DeleteEntryQueryMessage {
    /**
     * Get identifier of the entry to delete.
     *
     * @return identifier of the entry
     * @throws ReadValueException if error occurs reading value from message
     */
    String getEntryId() throws ReadValueException;
}
