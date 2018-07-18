package info.smart_tools.smartactors.endpoint_components_netty.inbound_netty_message_reference_count_management_components;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IFunction;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.FunctionExecutionException;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandlerCallback;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.exception.MessageHandlerException;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.helpers.IBypassMessageHandler;
import io.netty.util.IllegalReferenceCountException;
import io.netty.util.ReferenceCounted;

/**
 * Message handler that increases reference counter of a Netty message associated with a message context before the next
 * handler call and decreases that counter if the next handler throws an exception.
 *
 * <p>
 *  This handler is meant to be used before a handler that delegates message handling to another thread followed by
 *  {@link ReleaseNettyMessageHandler a handler that decreases reference counter after the next handlers finish
 *  synchronous handling}.
 * </p>
 *
 * @param <TContext> message context type
 */
public class RetainNettyMessageHandler<TContext extends IMessageContext>
        implements IBypassMessageHandler<TContext> {
    private final IFunction<TContext, ReferenceCounted> messageExtractor;

    /**
     * The constructor.
     *
     * @param messageExtractor function extracting a Netty message from message context
     */
    public RetainNettyMessageHandler(final IFunction<TContext, ReferenceCounted> messageExtractor) {
        this.messageExtractor = messageExtractor;
    }

    @Override
    public void handle(final IMessageHandlerCallback<TContext> next, final TContext context)
            throws MessageHandlerException {
        ReferenceCounted message;

        try {
            message = messageExtractor.execute(context);
        } catch (FunctionExecutionException | InvalidArgumentException e) {
            throw new MessageHandlerException(e);
        }

        message.retain();

        try {
            next.handle(context);
        } catch (MessageHandlerException | RuntimeException | Error e) {
            try {
                message.release();
            } catch (IllegalReferenceCountException ee) {
                e.addSuppressed(ee);
            }

            throw e;
        }
    }
}
