package info.smart_tools.smartactors.endpoint_components_netty.http_method_setter;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_byte_array.IMessageByteArray;
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
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;

/**
 * Sets method of outbound HTTP request.
 *
 * <p>
 * Expected request environment content:
 * <pre>
 * {
 *     "context": {
 *         "httpMethod": "PATCH",
 *         ...
 *     },
 *     ...
 * }
 * </pre>
 * </p>
 *
 * @param <TReq>
 * @param <TCtx>
 */
public class HttpMethodSetter<TReq extends IMessageByteArray<? extends HttpRequest>, TCtx>
        implements IBypassMessageHandler<IDefaultMessageContext<IObject, TReq, TCtx>> {
    private final IFieldName contextFN;
    private final IFieldName httpMethodFN;

    public HttpMethodSetter()
            throws ResolutionException {
        contextFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "context");
        httpMethodFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "httpMethod");
    }

    @Override
    public void handle(
            final IMessageHandlerCallback<IDefaultMessageContext<IObject, TReq, TCtx>> next,
            final IDefaultMessageContext<IObject, TReq, TCtx> context)
                throws MessageHandlerException {
        try {
            IObject reqContext = (IObject) context.getSrcMessage().getValue(contextFN);
            String methodName = (String) reqContext.getValue(httpMethodFN);

            if (null != methodName) {
                context.getDstMessage().getMessage().setMethod(HttpMethod.valueOf(methodName.toUpperCase()));
            }
        } catch (ReadValueException | InvalidArgumentException e) {
            throw new MessageHandlerException(e);
        }

        next.handle(context);
    }
}
