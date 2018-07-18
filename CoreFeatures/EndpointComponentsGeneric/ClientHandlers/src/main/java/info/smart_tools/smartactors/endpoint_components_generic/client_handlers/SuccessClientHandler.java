package info.smart_tools.smartactors.endpoint_components_generic.client_handlers;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.endpoint_interfaces.iclient_callback.IClientCallback;
import info.smart_tools.smartactors.endpoint_interfaces.iclient_callback.exceptions.ClientCallbackException;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IDefaultMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandlerCallback;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.exception.MessageHandlerException;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.helpers.ITerminalMessageHandler;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.DeleteValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;

/**
 * Calls {@link IClientCallback#onSuccess(IObject, IObject)} method of request callback.
 *
 * <p>
 *  Expected content of internal inbound message environment (destination message):
 * <pre>
 *  {
 *    ...
 *
 *    "request": { // The original request object
 *      "callback": {{@link IClientCallback} instance},
 *
 *      ...
 *    }
 *  }
 * </pre>
 * </p>
 *
 * @param <TResp>
 * @param <TResp>
 */
public class SuccessClientHandler<TResp, TCtx>
        implements ITerminalMessageHandler<IDefaultMessageContext<TResp, IObject, TCtx>> {
    private final IFieldName callbackFN;
    private final IFieldName requestFN;

    /**
     * The constructor.
     *
     * @throws ResolutionException if error occurs resolving any dependency
     */
    public SuccessClientHandler()
            throws ResolutionException {
        callbackFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "callback");
        requestFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "request");
    }

    @Override
    public void handle(
        final IMessageHandlerCallback<IMessageContext> next,
        final IDefaultMessageContext<TResp, IObject, TCtx> context)
            throws MessageHandlerException {
        IObject env = context.getDstMessage();
        IObject request;

        IClientCallback callback;

        try {
            request = (IObject) env.getValue(requestFN);
            callback = (IClientCallback) request.getValue(callbackFN);
        } catch (ReadValueException | InvalidArgumentException e) {
            throw new MessageHandlerException(e);
        }

        try {
            callback.onSuccess(request, env);
        } catch (ClientCallbackException e) {
            throw new MessageHandlerException(e);
        } finally {
            try {
                request.deleteField(callbackFN);
            } catch (DeleteValueException | InvalidArgumentException e) {
                //
            }
        }
    }
}
