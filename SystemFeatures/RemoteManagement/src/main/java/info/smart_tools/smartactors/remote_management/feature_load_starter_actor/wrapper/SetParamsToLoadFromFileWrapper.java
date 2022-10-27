package info.smart_tools.smartactors.remote_management.feature_load_starter_actor.wrapper;

import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;

public interface SetParamsToLoadFromFileWrapper {

    /**
     * Set the name of feature file to use in onNewFile chain
     * @param fileName the name of feature file
     * @throws ChangeValueException if error during set is occurred
     */
    void setFileName(String fileName)
            throws ChangeValueException;

    /**
     * Set the directory to use in onNewFile chain
     * @param directory the directory for onNewFile chain
     * @throws ChangeValueException if error during change is occurred
     */
    void setObservedDirectory(String directory)
            throws ChangeValueException;

    /**
     * Get feature location for loading
     * @return feature location
     * @throws ReadValueException if error during get is occurred
     */
    String getFeatureLocation()
            throws ReadValueException;

    /**
     * Get destination server directory to store loading feature
     * @return destination feature directory on server
     * @throws ReadValueException if error during get is occurred
     */
    String getFeatureDestinationPath()
            throws ReadValueException;
}
