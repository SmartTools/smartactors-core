package info.smart_tools.smartactors.endpoint_interfaces.imessage_handler;


import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.exception.MessageHandlerException;

/**
 * Interface for an object that handles a source message.
 *
 * <p>
 *  Message handling may include one or more actions of the following kinds:
 *
 *  <ul>
 *      <li>
 *          Environment setup for consequent handlers. E.g. execute next handler in another thread or setup some
 *          thread-local variables.
 *      </li>
 *      <li>
 *          Context transformation.
 *      </li>
 *      <li>
 *          Source message transformation. Including aggregation/split (in this case the next handler may be called more
 *          or less times than previous one).
 *      </li>
 *      <li>
 *          Creation of destination message.
 *      </li>
 *      <li>
 *          Destination message transformation. Created destination message may be transformed using some data taken
 *          from source message and/or connection context. Examples: parse headers of inbound HTTP message and store
 *          them in internal message environment or vise versa setting headers of outbound HTTP message from data stored
 *          in internal message.
 *      </li>
 *      <li>
 *          Sending destination message. Such action is most probably performed by the last handler of pipeline.
 *      </li>
 *  </ul>
 * </p>
 *
 * <p>
 *  Message handlers are joined into pipelines. Message handler may let the next handler of pipeline to continue message
 *  handling by calling {@link IMessageHandlerCallback callback} passed to it. Message handler may call that callback
 *  zero, one or more than one time for a call of handler method.
 * </p>
 *
 * <p>
 *  In case of inbound message the external inbound message (concrete type depends on endpoint implementation) is source
 *  message for the first {@link IMessageHandler message handler} and the internal message environment is destination
 *  message produced by some of intermediate message handlers.
 *  One of the pipeline handlers (probably the last one) may send the internal message.
 * </p>
 *
 * <p>
 *  In case of outbound message the internal message is a source message for the first message handler and the endpoint
 *  implementation depended representation of external message is the destination message produced by some of
 *  intermediate message handlers.
 *  One of the pipeline handlers (probably the last one) may send the external message in
 *  endpoint-implementation-specific way.
 * </p>
 *
 * <p>
 *  All parameters passed between message handlers are packed into a single object called {@link IMessageContext
 *  message context}. Concrete message context types are defined by {@code TContext} and {@code TNextContext} type
 *  parameters of concrete handler class. Caller of {@link #handle(IMessageHandlerCallback, IMessageContext)} method
 *  should guarantee that passed context of type {@code TContext} is able to be converted to {@code TNextContext} type
 *  by call of {@link IMessageContext#cast(Class)} method.
 * </p>
 *
 * @param <TContext>     type of message context
 * @param <TNextContext> type of message context expected by the next handler
 */
public interface IMessageHandler<TContext extends IMessageContext, TNextContext extends IMessageContext> {
    /**
     * Handle the message.
     *
     * @param next       callback that should handle the destination message after this handler
     * @param context    message context
     * @throws MessageHandlerException if any error occurs handling te message
     */
    void handle(IMessageHandlerCallback<TNextContext> next, TContext context)
            throws MessageHandlerException;
}
