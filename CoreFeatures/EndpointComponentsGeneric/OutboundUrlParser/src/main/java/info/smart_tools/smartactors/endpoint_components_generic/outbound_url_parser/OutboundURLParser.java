package info.smart_tools.smartactors.endpoint_components_generic.outbound_url_parser;

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

import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Handler that parses URL stored in outbound internal message.
 *
 * <p>
 *  Source message
 *
 *  <pre>
 *   {
 *     ...
 *
 *     "url": "http://smart-tools.info:8080/tools/smart?reallySmart=no#"
 *   }
 *  </pre>
 *
 *  will be transformed to:
 *
 *  <pre>
 *   {
 *     ...
 *
 *     "url": "http://smart-tools.info:8080/tools/smart?reallySmart=no#",
 *
 *     "protocol": "http",
 *     "port": 8080,
 *     "path": "/tools/smart?reallySmart=no",
 *     "host": "smart-tools.info",
 *     "address": {{@link InetSocketAddress} instance with host="smart-tools.info" and port=8080}
 *   }
 *  </pre>
 * </p>
 *
 * @param <TDst>
 * @param <TCtx>
 */
public class OutboundURLParser<TDst, TCtx>
        implements IBypassMessageHandler<IDefaultMessageContext<IObject, TDst, TCtx>> {
    private final IFieldName urlFN;
    private final IFieldName addressFN;
    private final IFieldName pathFN;
    private final IFieldName hostFN;
    private final IFieldName protocolFN;
    private final IFieldName portFN;

    /**
     * The constructor.
     *
     * @throws ResolutionException if error occurs resolving any dependency
     */
    public OutboundURLParser() throws ResolutionException {
        portFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "port");
        protocolFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "protocol");
        hostFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "host");
        pathFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "path");
        addressFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "address");
        urlFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "url");
    }

    @Override
    public void handle(
            final IMessageHandlerCallback<IDefaultMessageContext<IObject, TDst, TCtx>> next,
            final IDefaultMessageContext<IObject, TDst, TCtx> context)
                throws MessageHandlerException {
        IObject msg = context.getSrcMessage();

        try {
            URL url = new URL((String) msg.getValue(urlFN));

            msg.setValue(addressFN, new InetSocketAddress(url.getHost(), url.getPort()));
            msg.setValue(pathFN, url.getFile()); // Store both path and query strings in "path" field
            msg.setValue(hostFN, url.getHost());
            msg.setValue(protocolFN, url.getProtocol());
            msg.setValue(portFN, url.getPort());
        } catch (MalformedURLException | ReadValueException | InvalidArgumentException | ChangeValueException e) {
            throw new MessageHandlerException(e);
        }

        next.handle(context);
    }
}
