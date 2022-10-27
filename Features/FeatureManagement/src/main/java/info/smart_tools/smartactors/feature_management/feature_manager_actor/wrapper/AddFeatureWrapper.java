package info.smart_tools.smartactors.feature_management.feature_manager_actor.wrapper;

import info.smart_tools.smartactors.feature_management.interfaces.ifeature.IFeature;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;

import java.util.Collection;

/**
 * Interface of wrapper for {@link info.smart_tools.smartactors.feature_management.feature_manager_actor.FeatureManagerActor}
 */
public interface AddFeatureWrapper {

    /**
     * Gets collection of features from the message
     * @return collection of features
     * @throws ReadValueException if any errors occurred on wrapper reading
     */
    Collection<IFeature> getFeatures()
            throws ReadValueException;

    /**
     * Gets instance of {@link IMessageProcessor}
     * @return the message processor
     * @throws ReadValueException if any errors occurred on wrapper reading
     */
    IMessageProcessor getMessageProcessor()
            throws ReadValueException;

    /**
     * Gets name of chain for parallel processing
     * @return the name of chain
     * @throws ReadValueException if any errors occurred on wrapper reading
     */
    String getScatterChainName()
            throws ReadValueException;
}
