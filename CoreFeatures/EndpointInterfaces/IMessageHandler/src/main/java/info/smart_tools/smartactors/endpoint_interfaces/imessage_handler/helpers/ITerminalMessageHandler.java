package info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.helpers;

import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandler;

/**
 * A {@link IMessageHandler message handler} that does not pass a message to the nest handler.
 *
 * @param <TContext> message context type
 */
public interface ITerminalMessageHandler<TContext extends IMessageContext>
        extends IMessageHandler<TContext, IMessageContext> {
}
