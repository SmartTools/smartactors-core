package info.smart_tools.smartactors.endpoint_components_generic.client_handlers;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.endpoint_interfaces.iclient_callback.IClientCallback;
import info.smart_tools.smartactors.endpoint_interfaces.iclient_callback.exceptions.ClientCallbackException;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IDefaultMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandlerCallback;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.exception.MessageHandlerException;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.helpers.IBypassMessageHandler;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;

/**
 * Calls {@link IClientCallback#onStart(IObject)} method of callback passed with request object.
 *
 * <p>
 *  Also calls {@link IClientCallback#onError(IObject, Throwable)} method of the same callback if any of consequent
 *  handlers throws (<b>unless the callback was removed from request object</b>).
 * </p>
 *
 * @param <TCtx>
 * @param <TDst>
 */
public class StartClientHandler<TCtx, TDst>
        implements IBypassMessageHandler<IDefaultMessageContext<IObject, TDst, TCtx>> {
    private final IFieldName callbackFN;

    /**
     * The constructor.
     *
     * @throws ResolutionException if error occurs resolving any dependency
     */
    public StartClientHandler()
            throws ResolutionException {
        callbackFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "callback");
    }

    @Override
    public void handle(
        final IMessageHandlerCallback<IDefaultMessageContext<IObject, TDst, TCtx>> next,
        final IDefaultMessageContext<IObject, TDst, TCtx> context)
            throws MessageHandlerException {
        IObject request = context.getSrcMessage();
        IClientCallback callback;

        try {
            callback = (IClientCallback) request.getValue(callbackFN);

            callback.onStart(request);
        } catch (ReadValueException | InvalidArgumentException | ClientCallbackException e) {
            throw new MessageHandlerException(e);
        }

        try {
            next.handle(context);
        } catch (MessageHandlerException | RuntimeException | Error e) {

            try {
                if (request.getValue(callbackFN) == callback) {
                    callback.onError(request, e);
                }
            } catch (ReadValueException | InvalidArgumentException | ClientCallbackException | RuntimeException | Error ee) {
                e.addSuppressed(ee);
            }

            throw e;
        }
    }
}
