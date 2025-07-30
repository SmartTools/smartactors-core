package info.smart_tools.smartactors.feature_management.feature_creator_actor.wrapper;

import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;

import java.util.List;

/**
 * Interface of wrapper for {@link info.smart_tools.smartactors.feature_management.feature_creator_actor.FeaturesCreatorActor}
 */
public interface CreateMessageWrapper {

    /**
     * Gets name of file
     * @return the name of file
     * @throws ReadValueException if any errors occurred on wrapper reading
     */
    String getFileName()
            throws ReadValueException;

    /**
     * Gets location of observed directory
     * @return the location of observed directory
     * @throws ReadValueException if any errors occurred on wrapper reading
     */
    String getObservedDirectory()
            throws ReadValueException;

    /**
     * Puts feature descriptions to the message
     * @param features the list of feature descriptions
     * @throws ChangeValueException if any errors occurred on writing to the wrapper
     */
    void setJsonFeaturesDescription(List<IObject> features)
            throws ChangeValueException;

    /**
     * Puts repository descriptions to the message
     * @param repositories the list of repository descriptions
     * @throws ChangeValueException if any errors occurred on writing to the wrapper
     */
    void setJsonRepositoriesDescription(List<IObject> repositories)
            throws ChangeValueException;

}
