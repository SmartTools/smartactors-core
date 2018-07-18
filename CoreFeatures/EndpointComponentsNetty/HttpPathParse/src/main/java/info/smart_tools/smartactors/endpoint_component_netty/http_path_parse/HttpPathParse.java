package info.smart_tools.smartactors.endpoint_component_netty.http_path_parse;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.endpoint_components_generic.parse_tree.IParseTree;
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

public class HttpPathParse<TReq extends HttpRequest, TCtx>
        implements IBypassMessageHandler<IDefaultMessageContext<IInboundMessageByteArray<TReq>, IObject, TCtx>> {
    private final IFieldName messageFieldName;
    private final IParseTree tree;

    /**
     * The constructor.
     *
     * @param templates list of path templates
     * @throws ResolutionException if error occurs resolving any dependencies
     */
    public HttpPathParse(final List<String> templates) throws ResolutionException {
        messageFieldName = IOC.resolve(Keys.getOrAdd("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "message");
        this.tree = IOC.resolve(Keys.getOrAdd(IParseTree.class.getCanonicalName()));
        if (null == templates) {
            return;
        }
        for (String template : templates) {
            tree.addTemplate(template);
        }
    }


    @Override
    public void handle(
        final IMessageHandlerCallback<IDefaultMessageContext<IInboundMessageByteArray<TReq>, IObject, TCtx>> next,
        final IDefaultMessageContext<IInboundMessageByteArray<TReq>, IObject, TCtx> context)
            throws MessageHandlerException {
        try {
            HttpRequest request = context.getSrcMessage().getMessage();
            IObject message = (IObject) context.getDstMessage().getValue(messageFieldName);

            if (!request.uri().contains("/")) {
                return;
            }

            QueryStringDecoder decoder = new QueryStringDecoder(
                    request.uri().substring(request.uri().indexOf("/")));
            Map<String, String> parameters = tree.match(decoder.path());
            parameters.forEach(
                    (k, v) -> {
                        try {
                            IFieldName fieldName = IOC.resolve(Keys.getOrAdd("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), k);
                            message.setValue(fieldName, v);
                        } catch (ResolutionException | ChangeValueException | InvalidArgumentException e) {
                            throw new RuntimeException(e);
                        }
                    }
            );
        } catch (ReadValueException | InvalidArgumentException e) {
            throw new MessageHandlerException(e);
        }
        next.handle(context);
    }
}