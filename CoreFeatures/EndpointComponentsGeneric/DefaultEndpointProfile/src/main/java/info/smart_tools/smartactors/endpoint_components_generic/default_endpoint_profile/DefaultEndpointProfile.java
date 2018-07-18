package info.smart_tools.smartactors.endpoint_components_generic.default_endpoint_profile;

import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_pipeline.IEndpointPipeline;
import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_pipeline_set.IEndpointPipelineSet;
import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_pipeline_set.exceptions.PipelineCreationException;
import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_pipeline_set.exceptions.PipelineDescriptionNotFoundException;
import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_profile.IEndpointProfile;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageContext;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;

import java.util.Map;

/**
 * Default implementation of {@link IEndpointProfile endpoint profile}. Stores some pipeline descriptions, has the only
 * parent profile.
 */
public class DefaultEndpointProfile implements IEndpointProfile {
    private final IEndpointProfile parent;
    private final Map<String, IObject> pipelineDescriptions;

    /**
     * The constructor.
     *
     * @param parent               parent profile
     * @param pipelineDescriptions map from pipeline id to pipeline description
     */
    public DefaultEndpointProfile(final IEndpointProfile parent, final Map<String, IObject> pipelineDescriptions) {
        this.parent = parent;
        this.pipelineDescriptions = pipelineDescriptions;
    }

    @Override
    public <T extends IMessageContext> IEndpointPipeline<T> createPipeline(
        final String id,
        final IEndpointPipelineSet pipelineSet,
        final IObject endpointConfig)
            throws PipelineDescriptionNotFoundException, PipelineCreationException {

        IObject pipelineDesc = pipelineDescriptions.get(id);

        if (null != pipelineDesc) {
            try {
                return IOC.resolve(Keys.getOrAdd("create endpoint pipeline"), pipelineDesc, endpointConfig, pipelineSet);
            } catch (ResolutionException e) {
                throw new PipelineCreationException(e);
            }
        }

        return parent.createPipeline(id, pipelineSet, endpointConfig);
    }
}
