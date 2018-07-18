package info.smart_tools.smartactors.endpoint_components_netty.inbound_netty_message_reference_count_management_components.message_extractors;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IFunction;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.FunctionExecutionException;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IDefaultMessageContext;
import io.netty.util.ReferenceCounted;

/**
 * Function that extracts a outbound Netty reference counted message from message context.
 *
 * @param <TContext> concrete context type
 */
public class OutboundMessageExtractor<TContext extends IDefaultMessageContext<?, ? extends ReferenceCounted, ?>>
        implements IFunction<TContext, ReferenceCounted> {
    @Override
    public ReferenceCounted execute(final TContext context)
            throws FunctionExecutionException, InvalidArgumentException {
        return context.getDstMessage();
    }
}
