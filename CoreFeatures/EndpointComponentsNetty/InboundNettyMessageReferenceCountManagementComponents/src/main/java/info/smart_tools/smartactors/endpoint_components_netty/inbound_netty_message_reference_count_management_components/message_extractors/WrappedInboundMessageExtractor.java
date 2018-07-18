package info.smart_tools.smartactors.endpoint_components_netty.inbound_netty_message_reference_count_management_components.message_extractors;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IFunction;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.FunctionExecutionException;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_byte_array.IMessageByteArray;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IDefaultMessageContext;
import io.netty.util.ReferenceCounted;

/**
 * Function that extracts a inbound wrapped Netty reference counted message from message context.
 *
 * @param <T> Netty message type
 * @param <TCtx> context type
 */
public class WrappedInboundMessageExtractor
        <T extends ReferenceCounted, TCtx extends IDefaultMessageContext<? extends IMessageByteArray<T>, ?, ?>>
        implements IFunction<TCtx, T> {
    @Override
    public T execute(final TCtx ctx)
            throws FunctionExecutionException, InvalidArgumentException {
        return ctx.getSrcMessage().getMessage();
    }
}
