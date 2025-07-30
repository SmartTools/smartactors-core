package info.smart_tools.smartactors.feature_management.directory_watcher_actor.wrapper;

import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;

/**
 * Interface of wrapper for {@link info.smart_tools.smartactors.feature_management.directory_watcher_actor.RuntimeDirectoryFeatureTracker}
 */
public interface StartWatchingWrapper {

    /**
     * Gets location of observed directory
     * @return the location of observed directory
     * @throws ReadValueException if any errors occurred on wrapper reading
     */
    String getObservedDirectory()
            throws ReadValueException;

    /**
     * Gets name of execution chain
     * @return the name of execution chain
     * @throws ReadValueException if any errors occurred on wrapper reading
     */
    String getExecutionChain()
            throws ReadValueException;

    /**
     * Gets field name for file name
     * @return the field name for file name
     * @throws ReadValueException if any errors occurred on wrapper reading
     */
    String getFileNameFieldName()
            throws ReadValueException;

    /**
     * Gets field name for field of observed directory
     * @return the field name for field of observed directory
     * @throws ReadValueException if any errors occurred on wrapper reading
     */
    String getObservedDirectoryFieldName()
            throws ReadValueException;
}
