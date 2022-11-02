package info.smart_tools.smartactors.remote_management.feature_load_starter_actor.wrapper;

import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;

import java.util.List;

public interface SetParamsToLoadFromRepositoryWrapper {

    /**
     * Puts features description to the message
     * @param features the list of feature descriptions
     * @throws ChangeValueException if any errors occurred on writing to the wrapper
     */
    void setFeaturesDescription(List<IObject> features)
            throws ChangeValueException;

    /**
     * Puts repositories description to the message
     * @param repositories the list of repository descriptions
     * @throws ChangeValueException if any errors occurred on writing to the wrapper
     */
    void setRepositoriesDescription(List<IObject> repositories)
            throws ChangeValueException;

    /**
     * Gets features description from the message
     * @return the list of feature descriptions
     * @throws ReadValueException if any errors occurred on wrapper reading
     */
    List<IObject> getFeaturesDescription()
            throws ReadValueException;

    /**
     * Gets repositories description from the message
     * @return the list of repository descriptions
     * @throws ReadValueException if any errors occurred on wrapper reading
     */
    List<IObject> getRepositoriesDescription()
            throws ReadValueException;

}
