package info.smart_tools.smartactors.endpoint_components_netty.http_query_string_parser;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_byte_array.IInboundMessageByteArray;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IDefaultMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandlerCallback;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.exception.MessageHandlerException;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.helpers.IBypassMessageHandler;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.util.List;
import java.util.Map;

/**
 * Message handler that parses query string of the inbound HTTP request and stores query parameter values in the
 * internal message.
 *
 * @param <TReq>
 * @param <TCtx>
 */
public class HttpQueryStringParser<TReq extends HttpRequest, TCtx>
        implements IBypassMessageHandler<IDefaultMessageContext<IInboundMessageByteArray<TReq>, IObject, TCtx>> {
    private final IFieldName messageFieldName;

    /**
     * The constructor.
     *
     * @throws ResolutionException if error occurs resolving any dependencies
     */
    public HttpQueryStringParser()
            throws ResolutionException {
        messageFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "message");
    }

    @Override
    public void handle(
            final IMessageHandlerCallback<IDefaultMessageContext<IInboundMessageByteArray<TReq>, IObject, TCtx>> next,
            final IDefaultMessageContext<IInboundMessageByteArray<TReq>, IObject, TCtx> context)
                throws MessageHandlerException {
        try {
            String uri = context.getSrcMessage().getMessage().uri();
            IObject message = (IObject) context.getDstMessage().getValue(messageFieldName);

            QueryStringDecoder queryStringDecoder = new QueryStringDecoder(uri, true);

            for (Map.Entry<String, List<String>> entry : queryStringDecoder.parameters().entrySet()) {
                List<String> values = entry.getValue();
                Object value = values.size() == 1 ? values.get(0) : values;
                IFieldName keyField = IOC.resolve(
                        Keys.getOrAdd("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"),
                        entry.getKey());
                message.setValue(keyField, value);
            }
        } catch (ReadValueException | InvalidArgumentException | ResolutionException | ChangeValueException e) {
            throw new MessageHandlerException(e);
        }

        next.handle(context);
    }
}
