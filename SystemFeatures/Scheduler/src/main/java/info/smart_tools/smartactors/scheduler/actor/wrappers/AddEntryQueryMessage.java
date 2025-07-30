package info.smart_tools.smartactors.scheduler.actor.wrappers;

import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;

/**
 * Wrapper for a message containing a query for creation of a new scheduler entry.
 */
public interface AddEntryQueryMessage {
    /**
     * Get arguments to create entry with.
     *
     * <p>
     * The arguments object should contain the {@code "strategy"} field with dependency name of the strategy to use for the new entry and
     * fields specific for the used strategy.
     * </p>
     *
     * <p>
     * <b>IMPORTANT:</b> The arguments object may be (and will be) used as a state object for scheduler entry so modification of this object
     * after creation of the entry is highly not recommended.
     * </p>
     *
     * @return entry creation arguments
     * @throws ReadValueException if error occurs reading the message
     */
    IObject getEntryArguments() throws ReadValueException;
}
