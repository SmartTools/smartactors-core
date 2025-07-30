package info.smart_tools.smartactors.feature_management.feature_manager_actor.wrapper;

import info.smart_tools.smartactors.feature_management.interfaces.ifeature.IFeature;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Interface of wrapper for {@link info.smart_tools.smartactors.feature_management.feature_manager_actor.FeatureManagerActor}
 */
public interface FeatureManagerStateWrapper {

    /**
     * Puts list of loaded feature to the message
     * @param loadedFeatures the list of loaded features
     * @throws ChangeValueException if any errors occurred on writing to the wrapper
     */
    void setLoadedFeatures(Collection<IFeature> loadedFeatures)
            throws ChangeValueException;

    /**
     * Puts list of failed features to the message
     * @param failedFeatures the list of failed features
     * @throws ChangeValueException if any errors occurred on writing to the wrapper
     */
    void setFailedFeatures(Collection<IFeature> failedFeatures)
            throws ChangeValueException;

    /**
     * Puts list of processing features to the message
     * @param processingFeature the list of processing features
     * @throws ChangeValueException if any errors occurred on writing to the wrapper
     */
    void setProcessingFeatures(Collection<IFeature> processingFeature)
            throws ChangeValueException;

    /**
     * Puts map of frozen feature message processors to the message
     * @param frozenFeature the map of frozen feature message processors - feature
     * @throws ChangeValueException if any errors occurred on writing to the wrapper
     */
    void setFrozenFeatureProcesses(Map<IMessageProcessor, IFeature> frozenFeature)
            throws ChangeValueException;

    /**
     * Puts map of frozen feature processing requests to the message
     * @param frozenRequest the map of frozen feature processing requests
     * @throws ChangeValueException if any errors occurred on writing to the wrapper
     */
    void setFrozenRequests(Map<IMessageProcessor, Set<IFeature>> frozenRequest)
            throws ChangeValueException;
}
