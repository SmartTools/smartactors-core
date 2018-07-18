package info.smart_tools.smartactors.endpoint_components_netty.http_status_setter;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_byte_array.IOutboundMessageByteArray;
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
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * Sets HTTP response status from status code stored in context of source message.
 *
 * <p>
 *  Source message environment should contain the following:
 *
 *  <pre>
 *      {
 *          ...
 *          "context": {
 *              ...
 *              "httpResponseStatusCode": 418
 *          }
 *      }
 *  </pre>
 *
 *  (where 418 is the code you want to return). If no code provided response code is not changed.
 * </p>
 *
 * @param <TResp>
 * @param <TCtx>
 */
public class HttpStatusSetter<TResp extends FullHttpResponse, TCtx>
        implements IBypassMessageHandler<IDefaultMessageContext<IObject, IOutboundMessageByteArray<TResp>, TCtx>> {
    private final IFieldName contextFN, statusCodeFN;

    public HttpStatusSetter()
            throws ResolutionException {
        statusCodeFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "httpResponseStatusCode");
        contextFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "context");
    }

    @Override
    public void handle(
        final IMessageHandlerCallback<IDefaultMessageContext<IObject, IOutboundMessageByteArray<TResp>, TCtx>> next,
        final IDefaultMessageContext<IObject, IOutboundMessageByteArray<TResp>, TCtx> context)
            throws MessageHandlerException {
        try {
            IObject messageContext = (IObject) context.getSrcMessage().getValue(contextFN);
            Number statusCode = (Number) messageContext.getValue(statusCodeFN);

            if (null != statusCode) {
                FullHttpResponse response = context.getDstMessage().getMessage();
                response.setStatus(HttpResponseStatus.valueOf(statusCode.intValue()));
            }
        } catch (InvalidArgumentException | ReadValueException | ClassCastException e) {
            throw new MessageHandlerException(e);
        }

        next.handle(context);
    }
}
