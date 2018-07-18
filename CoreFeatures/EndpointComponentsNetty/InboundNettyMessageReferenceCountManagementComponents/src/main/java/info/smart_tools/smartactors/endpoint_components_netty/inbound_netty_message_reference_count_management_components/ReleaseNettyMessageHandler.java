package info.smart_tools.smartactors.endpoint_components_netty.inbound_netty_message_reference_count_management_components;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IFunction;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.FunctionExecutionException;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandlerCallback;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.exception.MessageHandlerException;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.helpers.IBypassMessageHandler;
import io.netty.util.ReferenceCounted;

/**
 * Message handler that decreases reference counter of a Netty message associated with a message context when the next
 * handler exits synchronously.
 *
 * @param <TContext> message context type
 */
public class ReleaseNettyMessageHandler<TContext extends IMessageContext>
        implements IBypassMessageHandler<TContext> {
    private final IFunction<TContext, ReferenceCounted> messageExtractor;

    /**
     * The constructor.
     *
     * @param messageExtractor function extracting a Netty message from message context
     */
    public ReleaseNettyMessageHandler(final IFunction<TContext, ReferenceCounted> messageExtractor) {
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

        try {
            next.handle(context);
        } finally {
            message.release();
        }
    }
}
