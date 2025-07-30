package info.smart_tools.smartactors.scheduler.interfaces;

import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.scheduler.interfaces.exceptions.SchedulerEntryFilterException;

/**
 * Interface for a strategy that may cancel some state changes of a {@link ISchedulerEntry scheduler entry}.
 *
 * <p>
 *     Initial purpose of addition of such strategy is a requirement to add a ability to perform partial shutdown of a scheduling service
 *     i.e. make it execute only some entries marked with a special flag after shutdown request signal received.
 * </p>
 */
public interface ISchedulerEntryFilter {
    /**
     * Check if the entry scheduled for execution should be executed.
     *
     * @param entry    the entry scheduled for execution
     * @return {@code true} if the entry should be actually executed
     * @throws SchedulerEntryFilterException if any error occurs
     */
    boolean testExec(ISchedulerEntry entry) throws SchedulerEntryFilterException;

    /**
     * Check if the entry may be awaken.
     *
     * @param entry    the suspended entry
     * @return {@code true} if the entry should be awaken
     * @throws SchedulerEntryFilterException if any error occurs
     */
    boolean testAwake(ISchedulerEntry entry) throws SchedulerEntryFilterException;

    /**
     * Check if an entry should be restored from saved state.
     *
     * @param entryState    saved entry state
     * @return {@code true} if the entry should be restored
     * @throws SchedulerEntryFilterException if any error occurs
     */
    boolean testRestore(IObject entryState) throws SchedulerEntryFilterException;
}
