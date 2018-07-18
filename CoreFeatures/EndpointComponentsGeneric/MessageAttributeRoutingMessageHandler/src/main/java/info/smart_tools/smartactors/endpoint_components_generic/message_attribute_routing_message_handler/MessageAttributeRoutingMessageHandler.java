package info.smart_tools.smartactors.endpoint_components_generic.message_attribute_routing_message_handler;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IFunction;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.FunctionExecutionException;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandler;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandlerCallback;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.exception.MessageHandlerException;

import java.util.Map;

/**
 * Message handler that delegates message handling to one of other handlers depending on some attribute of message.
 *
 * <p>
 *  One of possible usages is choosing a HTTP message deserializer depending on "Content-Type" header value.
 * </p>
 *
 * @param <TContext>
 * @param <TNextContext>
 */
public class MessageAttributeRoutingMessageHandler<TContext extends IMessageContext, TNextContext extends IMessageContext>
        implements IMessageHandler<TContext, TNextContext> {
    private final IFunction<TContext, ?> attributeExtractor;
    private final Map<Object, IMessageHandler<TContext, TNextContext>> handlers;
    private final IMessageHandler<TContext, TNextContext> defaultHandler;

    /**
     * The constructor.
     *
     * @param attributeExtractor function that extracts message attribute from message context
     * @param handlers           map from attribute value to handler
     * @param defaultHandler     handler to use when no handler found in map
     */
    public MessageAttributeRoutingMessageHandler(
            final IFunction<TContext, ?> attributeExtractor,
            final Map<Object, IMessageHandler<TContext, TNextContext>> handlers,
            final IMessageHandler<TContext, TNextContext> defaultHandler) {
        this.attributeExtractor = attributeExtractor;
        this.handlers = handlers;
        this.defaultHandler = defaultHandler;
    }

    @Override
    public void handle(final IMessageHandlerCallback<TNextContext> next, final TContext context)
            throws MessageHandlerException {
        try {
            handlers.getOrDefault(attributeExtractor.execute(context), defaultHandler)
                    .handle(next, context);
        } catch (FunctionExecutionException | InvalidArgumentException e) {
            throw new MessageHandlerException("Error occurred extracting message attribute.", e);
        }
    }
}
