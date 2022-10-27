package info.smart_tools.smartactors.feature_management.load_feature_actor.wrapper;

import info.smart_tools.smartactors.feature_management.interfaces.ifeature.IFeature;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.task.interfaces.iqueue.IQueue;

/**
 * Interface of wrapper for {@link info.smart_tools.smartactors.feature_management.load_feature_actor.LoadFeatureActor}
 */
public interface LoadFeatureWrapper {

    /**
     * Gets feature from the message
     * @return the instance of {@link IFeature}
     * @throws ReadValueException if any errors occurred on wrapper reading
     */
    IFeature getFeature()
            throws ReadValueException;

    /**
     * Gets the afterFeaturesCallback queue from the message
     * @return the instance of {@link IQueue}
     * @throws ReadValueException if any errors occurred on wrapper reading
     */
    IQueue getAfterFeaturesCallbackQueue()
            throws ReadValueException;
}
