package info.smart_tools.smartactors.endpoint_components_generic.default_endpoint_profile;

import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_pipeline.IEndpointPipeline;
import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_pipeline_set.IEndpointPipelineSet;
import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_pipeline_set.exceptions.PipelineCreationException;
import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_pipeline_set.exceptions.PipelineDescriptionNotFoundException;
import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_profile.IEndpointProfile;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageContext;
import info.smart_tools.smartactors.iobject.iobject.IObject;

import java.util.Iterator;

/**
 * Implementation of {@link IEndpointProfile endpoint profile} that does not store any pipeline descriptions but has
 * multiple parent profiles.
 */
public class MultiParentEndpointProfile implements IEndpointProfile {
    private final Iterable<IEndpointProfile> parents;

    /**
     * The constructor.
     *
     * @param parents       collection of parent profiles
     */
    public MultiParentEndpointProfile(
            final Iterable<IEndpointProfile> parents) {
        this.parents = parents;
    }

    @Override
    public <T extends IMessageContext> IEndpointPipeline<T> createPipeline(
        final String id,
        final IEndpointPipelineSet pipelineSet,
        final IObject endpointConfig)
            throws PipelineDescriptionNotFoundException, PipelineCreationException {
        Iterator<IEndpointProfile> parentIterator = parents.iterator();

        while (parentIterator.hasNext()) {
            IEndpointProfile profile = parentIterator.next();

            try {
                return profile.createPipeline(id, pipelineSet, endpointConfig);
            } catch (PipelineDescriptionNotFoundException e) {
                if (!parentIterator.hasNext()) {
                    throw e;
                }
            }
        }

        return NullEndpointProfile.INSTANCE.createPipeline(id, pipelineSet, endpointConfig);
    }
}
