package info.smart_tools.smartactors.endpoint_component_netty.cookies_setter;

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
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;

import java.util.ArrayList;
import java.util.List;

/**
 * Cookies setter for outbound {@link HttpMessage HTTP messages}.
 * This implementation extract cookies from context of the environment and set them into response
 * Cookies should presents as {@link List <IObject>}
 * <pre>
 *     "cookies": [
 *         {
 *             "name": "nameOfTheCookie",
 *             "value": "valueOfTheCookie",
 *             "maxAge": "10",
 *             "path": "/"
 *         }
 *     ]
 * </pre>
 * If there is no maxAge, then cookie set as discard
 *
 * @param <TMsg>
 * @param <TCtx>
 */
public class CookiesSetter<TMsg extends HttpMessage, TCtx>
        implements IBypassMessageHandler<IDefaultMessageContext<IObject, IOutboundMessageByteArray<TMsg>, TCtx>> {
    private final IField contextField;
    private final IField cookiesField;
    private final IFieldName cookieName;
    private final IFieldName cookieValue;
    private final IFieldName maxAgeFieldName;
    private final IFieldName cookiePath;

    /**
     * The constructor.
     *
     * @throws ResolutionException if error occurs resolving any dependency
     */
    public CookiesSetter() throws ResolutionException {
        contextField = IOC.resolve(Keys.getOrAdd(IField.class.getCanonicalName()), "context");
        cookiesField = IOC.resolve(Keys.getOrAdd(IField.class.getCanonicalName()), "cookies");
        cookieName = IOC.resolve(Keys.getOrAdd("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "name");
        cookieValue = IOC.resolve(Keys.getOrAdd("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "value");
        cookiePath = IOC.resolve(Keys.getOrAdd("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "path");
        maxAgeFieldName = IOC.resolve(Keys.getOrAdd("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "maxAge");
    }

    @Override
    public void handle(
            final IMessageHandlerCallback<IDefaultMessageContext<IObject, IOutboundMessageByteArray<TMsg>, TCtx>> next,
            final IDefaultMessageContext<IObject, IOutboundMessageByteArray<TMsg>, TCtx> context)
            throws MessageHandlerException {
        try {
            HttpMessage httpMessage = context.getDstMessage().getMessage();
            IObject messageContext = contextField.in(context.getSrcMessage());
            List<IObject> cookies = cookiesField.in(messageContext, List.class);

            List<Cookie> cookiesList = new ArrayList<>();
            for (IObject cookieObject : cookies) {
                    Cookie cookie = new DefaultCookie(
                            cookieObject.getValue(cookieName).toString(),
                            cookieObject.getValue(cookieValue).toString());

                    String path = (String) cookieObject.getValue(cookiePath);
                    if (path != null) {
                        cookie.setPath(path);
                    }

                    Integer maxCookieAge = (Integer) cookieObject.getValue(maxAgeFieldName);
                    if (maxCookieAge != null) {
                        cookie.setMaxAge(maxCookieAge);
                    }
                    cookiesList.add(cookie);
            }
            httpMessage.headers().set(HttpHeaderNames.SET_COOKIE,
                    ServerCookieEncoder.STRICT.encode(cookiesList));
        } catch (Exception e) {
            throw new MessageHandlerException(e);
        }

        next.handle(context);
    }
}
