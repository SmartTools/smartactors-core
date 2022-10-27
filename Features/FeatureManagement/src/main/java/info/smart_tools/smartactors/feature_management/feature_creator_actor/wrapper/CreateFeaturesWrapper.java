package info.smart_tools.smartactors.feature_management.feature_creator_actor.wrapper;

import info.smart_tools.smartactors.feature_management.interfaces.ifeature.IFeature;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;

import java.util.Collection;
import java.util.List;

/**
 * Interface of wrapper for {@link info.smart_tools.smartactors.feature_management.feature_creator_actor.FeaturesCreatorActor}
 */
public interface CreateFeaturesWrapper {

    /**
     * Puts set of features to the message
     * @param features the set of features
     * @throws ChangeValueException if any errors occurred on writing to the wrapper
     */
    void setFeatures(Collection<IFeature> features)
            throws ChangeValueException;

    /**
     * Gets feature description from the message
     * @return the list of feature descriptions
     * @throws ReadValueException if any errors occurred on wrapper reading
     */
    List<IObject> getFeaturesDescription()
            throws ReadValueException;

    /**
     * Gets repository descriptions from the message
     * @return the list of repository descriptions
     * @throws ReadValueException if any errors occurred on wrapper reading
     */
    List<IObject> getRepositoriesDescription()
            throws ReadValueException;

    /**
     * Gets feature location
     * @return the feature location
     * @throws ReadValueException if any errors occurred on wrapper reading
     */
    String getFeatureDirectory()
            throws ReadValueException;
}
