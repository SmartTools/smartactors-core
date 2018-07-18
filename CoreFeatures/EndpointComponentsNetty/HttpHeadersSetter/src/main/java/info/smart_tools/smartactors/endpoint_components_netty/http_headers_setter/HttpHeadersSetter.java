package info.smart_tools.smartactors.endpoint_components_netty.http_headers_setter;

import info.smart_tools.smartactors.endpoint_interfaces.imessage_byte_array.IOutboundMessageByteArray;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IDefaultMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandlerCallback;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.exception.MessageHandlerException;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.helpers.IBypassMessageHandler;
import info.smart_tools.smartactors.iobject.ifield.IField;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;

import java.util.List;

/**
 * Headers setter for {@link FullHttpResponse}
 * This implementation extract headers from context of the environment and set them into response
 * Headers should presents as {@link List<IObject>}
 * <pre>
 *     "headers": [
 *         {
 *             "name": "nameOfTheHeader",
 *             "value": "valueOfTheHeader"
 *         }
 *     ]
 * </pre>
 */
public class HttpHeadersSetter<TMsg extends FullHttpMessage, TCtx>
        implements IBypassMessageHandler<IDefaultMessageContext<IObject, IOutboundMessageByteArray<TMsg>, TCtx>> {
    private final IField contextField;
    private final IField headersField;
    private final IFieldName headerName;
    private final IFieldName headerValue;

    /**
     * The constructor.
     *
     * @throws ResolutionException if error occurs resolving any dependency
     */
    public HttpHeadersSetter() throws ResolutionException {
        contextField = IOC.resolve(Keys.getOrAdd(IField.class.getCanonicalName()), "context");
        headersField = IOC.resolve(Keys.getOrAdd(IField.class.getCanonicalName()), "headers");
        headerName = IOC.resolve(Keys.getOrAdd("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "name");
        headerValue = IOC.resolve(Keys.getOrAdd("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "value");
    }

    @Override
    public void handle(
        final IMessageHandlerCallback<IDefaultMessageContext<IObject, IOutboundMessageByteArray<TMsg>, TCtx>> next,
        final IDefaultMessageContext<IObject, IOutboundMessageByteArray<TMsg>, TCtx> context)
            throws MessageHandlerException {
        try {
            FullHttpMessage httpMessage = context.getDstMessage().getMessage();
            IObject messageContext = contextField.in(context.getSrcMessage());

            httpMessage.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
            List<IObject> headers = headersField.in(messageContext, List.class);

            for (IObject header : headers) {
                    httpMessage.headers().set(String.valueOf(header.getValue(headerName)),
                            String.valueOf(header.getValue(headerValue)));
            }
            httpMessage.headers().set(HttpHeaderNames.CONTENT_LENGTH, httpMessage.content().readableBytes());
            httpMessage.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        } catch (Exception e) {
            throw new MessageHandlerException(e);
        }

        next.handle(context);
    }
}
