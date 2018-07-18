package info.smart_tools.smartactors.endpoint_components_generic.default_endpoint_profile;

import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_pipeline.IEndpointPipeline;
import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_pipeline_set.IEndpointPipelineSet;
import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_pipeline_set.exceptions.PipelineCreationException;
import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_pipeline_set.exceptions.PipelineDescriptionNotFoundException;
import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_profile.IEndpointProfile;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageContext;
import info.smart_tools.smartactors.iobject.iobject.IObject;

/**
 * Null-implementation of {@link IEndpointProfile endpoint profile}. Contains no pipeline descriptions, has no parent
 * profiles.
 */
public enum NullEndpointProfile implements IEndpointProfile {
    INSTANCE;

    @Override
    public <T extends IMessageContext> IEndpointPipeline<T> createPipeline(
        final String id,
        final IEndpointPipelineSet pipelineSet,
        final IObject endpointConfig)
            throws PipelineDescriptionNotFoundException, PipelineCreationException {
        throw new PipelineDescriptionNotFoundException("No description found for pipeline '" + id + "'.");
    }
}
