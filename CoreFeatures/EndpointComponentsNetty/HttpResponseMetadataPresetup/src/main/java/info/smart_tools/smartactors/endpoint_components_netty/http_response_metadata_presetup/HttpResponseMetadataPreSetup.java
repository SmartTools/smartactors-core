package info.smart_tools.smartactors.endpoint_components_netty.http_response_metadata_presetup;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
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

import java.util.ArrayList;

/**
 * Message handler that stores empty header and cookies lists for HTTP response in inbound request context.
 */
public class HttpResponseMetadataPreSetup implements
        IBypassMessageHandler<IDefaultMessageContext<?, IObject, ?>> {
    private final IFieldName contextFN, headersFN, cookiesFN;

    /**
     * The constructor.
     *
     * @throws ResolutionException if error occurs resolving any dependency
     */
    public HttpResponseMetadataPreSetup()
            throws ResolutionException {
        contextFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "context");
        cookiesFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "cookies");
        headersFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "headers");
    }

    @Override
    public void handle(
        final IMessageHandlerCallback<IDefaultMessageContext<?, IObject, ?>> next,
        final IDefaultMessageContext<?, IObject, ?> ctx)
            throws MessageHandlerException {
        try {
            IObject context = (IObject) ctx.getDstMessage().getValue(contextFN);
            context.setValue(headersFN, new ArrayList());
            context.setValue(cookiesFN, new ArrayList());
        } catch (ReadValueException | ChangeValueException | InvalidArgumentException e) {
            throw new MessageHandlerException(e);
        }

        next.handle(ctx);
    }
}
