package info.smart_tools.smartactors.endpoint_interfaces.iendpoint_profile;

import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_pipeline.IEndpointPipeline;
import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_pipeline_set.IEndpointPipelineSet;
import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_pipeline_set.exceptions.PipelineCreationException;
import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_pipeline_set.exceptions.PipelineDescriptionNotFoundException;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageContext;
import info.smart_tools.smartactors.iobject.iobject.IObject;

/**
 * Endpoint profile holds a set of {@link IEndpointPipeline endpoint pipeline} descriptions required for some kind of
 * endpoints.
 *
 * <p>
 *  Canonical profile description of configurable endpoint profile should look like the following:
 * </p>
 *
 * <pre>
 *  {
 *      "id": "profile-id",
 *      "extend": ["base-profile-id"],
 *      "pipelines": [
 *          {
 *              "id": "pipeline-id",
 *              ...
 *          },
 *          ...
 *      ]
 *  }
 * </pre>
 *
 * <p>
 *  Each of objects in "pipelines" array contains a canonical description of a {@link IEndpointPipeline endpoint pipeline}.
 *  Profile may extend some another profile.
 * </p>
 */
public interface IEndpointProfile {
    /**
     * Create a pipeline using description with given identifier.
     *
     * @param <T>               type of message context accepted by the pipeline
     * @param id                pipeline description identifier
     * @param pipelineSet       pipeline set (may be used to get dependent pipelines)
     * @param endpointConfig    endpoint instance configuration object
     * @return the created pipeline
     * @throws PipelineDescriptionNotFoundException if no description found
     * @throws PipelineCreationException if error occurs creating a pipeline
     */
    <T extends IMessageContext> IEndpointPipeline<T> createPipeline(String id, IEndpointPipelineSet pipelineSet, IObject endpointConfig)
            throws PipelineDescriptionNotFoundException, PipelineCreationException;
}
