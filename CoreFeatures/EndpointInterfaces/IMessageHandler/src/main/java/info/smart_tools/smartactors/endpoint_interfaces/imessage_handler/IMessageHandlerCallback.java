package info.smart_tools.smartactors.endpoint_interfaces.imessage_handler;

import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.exception.MessageHandlerException;

/**
 * Interface for a callback passed to {@link IMessageHandlerCallback}.
 *
 * @param <TContext> type of message context expected by the callback
 */
public interface IMessageHandlerCallback<TContext extends IMessageContext> {
    /**
     * Delegate message handling to the next handler.
     *
     * @param context message context
     * @throws MessageHandlerException if any error occurs
     */
    void handle(TContext context)
            throws MessageHandlerException;
}
