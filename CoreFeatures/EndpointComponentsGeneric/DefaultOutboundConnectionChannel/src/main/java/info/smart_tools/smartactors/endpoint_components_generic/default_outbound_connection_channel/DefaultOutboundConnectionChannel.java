package info.smart_tools.smartactors.endpoint_components_generic.default_outbound_connection_channel;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.FunctionExecutionException;
import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_pipeline.IEndpointPipeline;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IDefaultMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.exception.MessageHandlerException;
import info.smart_tools.smartactors.endpoint_interfaces.ioutbound_connection_channel.IOutboundConnectionChannel;
import info.smart_tools.smartactors.endpoint_interfaces.ioutbound_connection_channel.exceptions.OutboundMessageSendException;
import info.smart_tools.smartactors.iobject.iobject.IObject;

/**
 * Default implementation of {@link IOutboundConnectionChannel outbound connection channel}.
 *
 * <p>
 *  This implementation stores a endpoint pipeline and a concrete connection context it uses to send messages.
 * </p>
 *
 * @param <TCtx>
 */
public class DefaultOutboundConnectionChannel<TCtx> implements IOutboundConnectionChannel {
    private final IEndpointPipeline<IDefaultMessageContext<IObject, Void, TCtx>> pipeline;
    private final TCtx connectionContext;

    /**
     * The constructor.
     *
     * @param pipeline          pipeline accepting outbound messages
     * @param connectionContext connection context
     */
    public DefaultOutboundConnectionChannel(
            final IEndpointPipeline<IDefaultMessageContext<IObject, Void, TCtx>> pipeline,
            final TCtx connectionContext) {
        this.pipeline = pipeline;
        this.connectionContext = connectionContext;
    }

    @Override
    public void send(final IObject env)
            throws OutboundMessageSendException, InvalidArgumentException {
        try {
            IDefaultMessageContext<IObject, Void, TCtx> context = pipeline.getContextFactory().execute();

            context.setSrcMessage(env);
            context.setConnectionContext(connectionContext);

            pipeline.getInputCallback().handle(context);
        } catch (FunctionExecutionException | MessageHandlerException e) {
            throw new OutboundMessageSendException(e);
        }
    }
}
