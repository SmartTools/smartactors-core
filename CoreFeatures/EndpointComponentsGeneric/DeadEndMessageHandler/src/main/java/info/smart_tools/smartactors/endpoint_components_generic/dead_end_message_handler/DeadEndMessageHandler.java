package info.smart_tools.smartactors.endpoint_components_generic.dead_end_message_handler;

import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandler;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandlerCallback;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.exception.MessageHandlerException;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.helpers.ITerminalMessageHandler;

/**
 * {@link IMessageHandler Message handler} that does nothing.
 *
 * <p>
 *  Should be used as terminal handler when no other terminal handler fits purposes of a pipeline.
 * </p>
 *
 * @param <T>
 */
public class DeadEndMessageHandler<T extends IMessageContext> implements ITerminalMessageHandler<T> {

    @Override
    public void handle(
            final IMessageHandlerCallback<IMessageContext> next,
            final T context)
            throws MessageHandlerException {
    }
}
