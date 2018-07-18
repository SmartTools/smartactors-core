package info.smart_tools.smartactors.endpoints_generic.generic_outbound_endpoint;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecuteException;
import info.smart_tools.smartactors.base.iup_counter.IUpCounter;
import info.smart_tools.smartactors.base.iup_counter.exception.UpCounterCallbackExecutionException;
import info.smart_tools.smartactors.endpoint_components_generic.default_outbound_connection_channel.DefaultOutboundConnectionChannel;
import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_pipeline.IEndpointPipeline;
import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_pipeline_set.IEndpointPipelineSet;
import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_pipeline_set.exceptions.PipelineCreationException;
import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_pipeline_set.exceptions.PipelineDescriptionNotFoundException;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IDefaultMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.ioutbound_connection_channel.IOutboundConnectionChannel;
import info.smart_tools.smartactors.endpoint_interfaces.ioutbound_connection_channel.IOutboundConnectionChannelListener;
import info.smart_tools.smartactors.endpoint_interfaces.ioutbound_connection_channel.exceptions.OutboundChannelListenerException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;

/**
 * Simple endpoint that sends outbound messages.
 *
 * <p>
 *  This endpoint registers a global outbound channel that sends a message as source message to specified pipeline.
 *  {@link GenericOutboundEndpoint} is not aware of what does a pipeline do when it receives a message so the pipeline
 *  may perform any endpoint-implementation-specific actions on both initialization and message processing stages.
 *  The endpoint implementation may even send a response to sent outbound message (probably using
 *  {@link info.smart_tools.smartactors.endpoint_interfaces.iclient_callback.IClientCallback client callback}).
 * </p>
 *
 * <p>
 *  Configuration format:
 * </p>
 *
 * <pre>
 *  {
 *    ...
 *
 *    "pipeline": ".. name of pipeline that will process outbound messages ..",
 *
 *    "channelId": ".. name of outbound channel that will be created ..",
 *
 *    "upcounter": ".. name of the up-counter (the outbound channel will be unregistered when shutdown happens) ..",
 *
 *    ...
 *  }
 * </pre>
 */
public class GenericOutboundEndpoint {
    /**
     * The constructor.
     *
     * @param config      endpoint configuration object
     * @param pipelineSet endpoint pipeline set
     * @throws ResolutionException if error occurs resolving any dependency
     * @throws ReadValueException if error occurs reading configuration object
     * @throws InvalidArgumentException if error occurs reading configuration object
     * @throws PipelineDescriptionNotFoundException if invalid pipeline identifier configured
     * @throws PipelineCreationException if error occurs creating a pipeline
     * @throws OutboundChannelListenerException if created channel is not accepted by global channel listener
     * @throws UpCounterCallbackExecutionException if error occurs registering upcounter callback
     */
    public GenericOutboundEndpoint(
            final IObject config,
            final IEndpointPipelineSet pipelineSet)
            throws ResolutionException, ReadValueException, InvalidArgumentException,
                    PipelineDescriptionNotFoundException, PipelineCreationException,
                    OutboundChannelListenerException, UpCounterCallbackExecutionException {
        IFieldName pipelineFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "pipeline");
        IFieldName channelIdFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "channelId");
        IFieldName upcounterFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "upcounter");

        IOutboundConnectionChannelListener channelListener
                = IOC.resolve(Keys.getOrAdd("global outbound connection channel storage channel listener"));

        String pipelineId = (String) config.getValue(pipelineFN);

        IEndpointPipeline<IDefaultMessageContext<IObject, Void, Void>> pipeline = pipelineSet.getPipeline(pipelineId);

        IOutboundConnectionChannel channel = new DefaultOutboundConnectionChannel<>(pipeline, null);

        IUpCounter upCounter = IOC.resolve(Keys.getOrAdd("upcounter"), config.getValue(upcounterFN));

        String channelId = (String) config.getValue(channelIdFN);

        channelListener.onConnect(channelId, channel);

        upCounter.onShutdownComplete(() -> {
            try {
                channelListener.onDisconnect(channelId);
            } catch (OutboundChannelListenerException e) {
                throw new ActionExecuteException(e);
            }
        });
    }
}
