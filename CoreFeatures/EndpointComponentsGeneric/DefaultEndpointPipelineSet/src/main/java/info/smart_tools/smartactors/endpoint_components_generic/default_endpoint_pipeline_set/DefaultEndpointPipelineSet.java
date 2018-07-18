package info.smart_tools.smartactors.endpoint_components_generic.default_endpoint_pipeline_set;

import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_pipeline.IEndpointPipeline;
import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_pipeline_set.IEndpointPipelineSet;
import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_pipeline_set.exceptions.PipelineCreationException;
import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_pipeline_set.exceptions.PipelineDescriptionNotFoundException;
import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_profile.IEndpointProfile;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageContext;
import info.smart_tools.smartactors.iobject.iobject.IObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Default implementation of {@link IEndpointPipelineSet endpoint pipeline set}.
 *
 * <p>
 *  This implementation uses synchronized data access even on read so it's recommended to query all required pipeline
 *  references on endpoint creation.
 * </p>
 */
public class DefaultEndpointPipelineSet implements IEndpointPipelineSet {
    private static final Object CREATION_IN_PROGRESS_SENTINEL = new Object();

    private final IObject endpointConfig;
    private final IEndpointProfile endpointProfile;

    private final Map<String, Object> pipelines;
    private final Object pipelinesLock = new Object();

    /**
     * The constructor.
     *
     * @param endpointConfig  endpoint configuration object
     * @param endpointProfile endpoint profile
     */
    public DefaultEndpointPipelineSet(final IObject endpointConfig, final IEndpointProfile endpointProfile) {
        this.endpointConfig = endpointConfig;
        this.endpointProfile = endpointProfile;

        pipelines = new HashMap<>();
    }

    @Override
    public <T extends IMessageContext> IEndpointPipeline<T> getPipeline(final String id)
            throws PipelineDescriptionNotFoundException, PipelineCreationException {
        synchronized (pipelinesLock) {
            Object pipeline = pipelines.get(id);

            if (CREATION_IN_PROGRESS_SENTINEL == pipeline) {
                throw new PipelineCreationException("Pipeline dependency loop including '" + id + "' pipeline detected.");
            }

            if (null == pipeline) {
                pipelines.put(id, CREATION_IN_PROGRESS_SENTINEL);

                try {
                    pipeline = endpointProfile.createPipeline(id, this, endpointConfig);
                    pipelines.put(id, pipeline);
                } catch (PipelineDescriptionNotFoundException | PipelineCreationException | RuntimeException | Error e) {
                    pipelines.remove(id);
                    throw e;
                }
            }

            return (IEndpointPipeline<T>) pipeline;
        }
    }
}
