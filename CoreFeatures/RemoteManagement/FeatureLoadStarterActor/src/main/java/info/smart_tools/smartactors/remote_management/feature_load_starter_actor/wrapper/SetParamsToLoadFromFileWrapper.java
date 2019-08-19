package info.smart_tools.smartactors.remote_management.feature_load_starter_actor.wrapper;

import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;

public interface SetParamsToLoadFromFileWrapper {

    /**
     *
     * @throws ChangeValueException
     */
    void setFileName(String fileName)
            throws ChangeValueException;

    /**
     *
     * @throws ChangeValueException
     */
    void setObservedDirectory(String directory)
            throws ChangeValueException;

    /**
     *
     * @return
     * @throws ReadValueException
     */
    String getFeatureLocation()
            throws ReadValueException;
}
