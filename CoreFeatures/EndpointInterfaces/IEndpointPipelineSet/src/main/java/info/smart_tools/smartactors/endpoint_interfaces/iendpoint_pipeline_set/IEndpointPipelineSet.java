package info.smart_tools.smartactors.endpoint_interfaces.iendpoint_pipeline_set;

import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_pipeline.IEndpointPipeline;
import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_pipeline_set.exceptions.PipelineCreationException;
import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_pipeline_set.exceptions.PipelineDescriptionNotFoundException;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageContext;

/**
 * Endpoint pipeline set. Represents a set of {@link IEndpointPipeline endpoint pipelines} used by a concrete endpoint
 * instance.
 */
public interface IEndpointPipelineSet {
    /**
     * Get pipeline with given identifier.
     *
     * @param id  pipeline identifier
     * @param <T> message context type
     * @return the pipeline
     * @throws PipelineDescriptionNotFoundException if no created pipeline and no pipeline description found for given id
     * @throws PipelineCreationException if there is a pipeline description for given id but error occurred creating it
     */
    <T extends IMessageContext> IEndpointPipeline<T> getPipeline(String id)
            throws PipelineDescriptionNotFoundException, PipelineCreationException;
}
