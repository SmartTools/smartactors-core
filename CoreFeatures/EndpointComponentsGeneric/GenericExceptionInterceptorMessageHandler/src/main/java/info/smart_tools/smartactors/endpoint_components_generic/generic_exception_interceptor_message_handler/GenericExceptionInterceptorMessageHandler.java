package info.smart_tools.smartactors.endpoint_components_generic.generic_exception_interceptor_message_handler;

import info.smart_tools.smartactors.base.interfaces.iaction.IBiAction;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IDefaultMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandlerCallback;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.exception.MessageHandlerException;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.helpers.IBypassMessageHandler;

/**
 * Message handler that executes some action when exception is thrown by one of next handlers.
 *
 * <p>
 *  Typical action is to close a connection when exception occurs. But sometimes it is possible to send a message
 *  containing information about exception.
 * </p>
 *
 * <p>
 *  This handler will not be able to execute the action if it occurs in code executed asynchronously by some of next
 *  handlers.
 * </p>
 *
 * @param <TCtx>
 * @param <T>
 */
public class GenericExceptionInterceptorMessageHandler<TCtx, T extends IDefaultMessageContext<?, ?, TCtx>>
        implements IBypassMessageHandler<T> {
    private final IBiAction<TCtx, Throwable> exceptionalAction;

    /**
     * The constructor.
     *
     * @param exceptionalAction action to execute when exception occurs
     */
    public GenericExceptionInterceptorMessageHandler(final IBiAction<TCtx, Throwable> exceptionalAction) {
        this.exceptionalAction = exceptionalAction;
    }

    @Override
    public void handle(final IMessageHandlerCallback<T> next, final T context) throws MessageHandlerException {
        TCtx ctx = context.getConnectionContext();

        try {
            next.handle(context);
        } catch (MessageHandlerException | RuntimeException | Error e) {
            try {
                exceptionalAction.execute(ctx, e);
            } catch (Exception ee) {
                e.addSuppressed(ee);
            }

            throw e;
        }
    }
}
