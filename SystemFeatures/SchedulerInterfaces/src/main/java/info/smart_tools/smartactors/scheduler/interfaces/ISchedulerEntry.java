package info.smart_tools.smartactors.scheduler.interfaces;

import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.scheduler.interfaces.exceptions.EntryPauseException;
import info.smart_tools.smartactors.scheduler.interfaces.exceptions.EntryScheduleException;
import info.smart_tools.smartactors.scheduler.interfaces.exceptions.EntryStorageAccessException;

/**
 * Interface of a scheduler entry.
 *
 * <p>
 *     When stored in database a entry is represented by a object like the following:
 * </p>
 *
 * <pre>
 *     {
 *         "entryId": "5cb03e18-e86a-4729-a281-e539fe2c2291",        // Unique identifier of the entry
 *         "strategy": "continuously repeat scheduling strategy",    // Dependency name of the {@link ISchedulingStrategy
 *                                                                      scheduling strategy}
 *         "message": {. . .},                                       // The message to send
 *
 *         . . .                                                     // Fields specific for scheduling strategy
 *     }
 * </pre>
 *
 * <p>
 *     A entry is <em>active</em> when there is a scheduled timer task associated with it.
 * </p>
 */
public interface ISchedulerEntry {
    /**
     * Get the state object of this entry.
     *
     * <p>
     *     Should contain at least the following fields:
     * </p>
     * <ul>
     *     <li>{@code "strategy"} - dependency name of the strategy used to determine time(s) when to send the message</li>
     *     <li>{@code "message"} - message to send</li>
     *     <li>{@code "entryId"} - identifier of the entry</li>
     * </ul>
     * <p>
     *     Field names ending with {@code "ID"} are reserved for storage primary keys. {@link ISchedulingStrategy Scheduling strategy} may
     *     use any other fields to store specific values.
     * </p>
     *
     * @return state of this entry
     */
    IObject getState();

    /**
     * Save this entry in database.
     *
     * @throws EntryStorageAccessException if cannot save the entry to database
     */
    void save() throws EntryStorageAccessException;

    /**
     * Cancel this entry. Will delete entry from database if it was saved in there.
     *
     * @throws EntryStorageAccessException if cannot delete the entry from database
     * @throws EntryScheduleException if error occurs cancelling the entry
     */
    void cancel() throws EntryStorageAccessException, EntryScheduleException;

    /**
     * Schedule this entry on given time.
     *
     * @param time    time (in milliseconds since epoch) to schedule this entry on
     * @throws EntryScheduleException if error occurs scheduling entry
     */
    void scheduleNext(long time) throws EntryScheduleException;

    /**
     * Get the last time this entry was scheduled on. Returns {@code -1} if the entry is just created or restored from state saved in
     * database.
     *
     * @return the last time (in milliseconds since epoch) this entry was scheduled on
     */
    long getLastTime();

    /**
     * @return identifier of this entry
     */
    String getId();

    /**
     * Deactivate this entry (but keep it in memory if there are other references to it or it is not saved in remote storage).
     *
     * @throws EntryStorageAccessException if any error occurs
     */
    void suspend() throws EntryStorageAccessException;

    /**
     * Activate the entry if it is not active.
     *
     * @throws EntryStorageAccessException if error occurs accessing entry storage
     * @throws EntryScheduleException if error occurs creating a timer task
     */
    void awake() throws EntryStorageAccessException, EntryScheduleException;

    /**
     * Check if this entry is active.
     *
     * @return {@code true} if the entry is active
     */
    boolean isAwake();

    /**
     * Pause entry execution.
     *
     * <p>
     * This method blocks if entry is being executed in another thread until execution is completed (but it's still possible to
     * pause/unpause entry at the same thread it's being executed - from scheduler action or scheduling strategy).
     * </p>
     *
     * <p>
     * When entry is paused it's action is not executed and {@link ISchedulingStrategy#postProcess(ISchedulerEntry)} is not called;
     * {@link ISchedulingStrategy#processPausedExecution(ISchedulerEntry)} is called instead.
     * </p>
     *
     * <p>
     * Caller of this method must keep strong reference to this entry and call {@link #unpause()} later.
     * </p>
     *
     * @throws EntryPauseException if any error occurs
     */
    void pause() throws EntryPauseException;

    /**
     * Unpause entry execution.
     *
     * @throws EntryPauseException if any error occurs
     */
    void unpause() throws EntryPauseException;
}
