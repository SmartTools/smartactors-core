package info.smart_tools.smartactors.scheduler.actor.wrappers;

import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;

/**
 * Wrapper for a message containing configuration updates for scheduler.
 */
public interface ConfigureQueryMessage {
    /**
     * Get configuration.
     *
     * <p>Current implementation supports the following parameters:</p>
     * <ul>
     *     <li>{@code "refreshRepeatInterval"} - time in millis between refresh iterations</li>
     *     <li>{@code "refreshAwakeInterval"} - time between refresh iteration start and scheduled time of the latest entry that will be
     *     awaken</li>
     *     <li>{@code "refreshSuspendInterval"} - time between refresh iteration start and scheduled time of the earliest entry that will be
     *     suspended</li>
     *     <li>{@code "minPageSize"} - minimal size of page downloaded from remote storage</li>
     *     <li>{@code "maxPageSize"} - maximal size of page downloaded from remote storage</li>
     *     <li>{@code "maxLocalEntries"} - maximal amount of entries in local storage after refresh iteration completion</li>
     * </ul>
     * <p>All parameters are optional and default to current values.</p>
     *
     * @return configuration object
     * @throws ReadValueException if error occurs reading value
     */
    IObject getConfig() throws ReadValueException;
}
