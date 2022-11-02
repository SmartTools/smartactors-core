package info.smart_tools.smartactors.http_endpoint.http_response_sender;

import info.smart_tools.smartactors.endpoint.interfaces.ichannel_handler.IChannelHandler;
import info.smart_tools.smartactors.endpoint.interfaces.iresponse.IResponse;
import info.smart_tools.smartactors.endpoint.interfaces.iresponse_sender.IResponseSender;
import info.smart_tools.smartactors.endpoint.interfaces.iresponse_sender.exceptions.ResponseSendingException;
import info.smart_tools.smartactors.http_endpoint.interfaces.icookies_extractor.ICookiesSetter;
import info.smart_tools.smartactors.http_endpoint.interfaces.icookies_extractor.exceptions.CookieSettingException;
import info.smart_tools.smartactors.http_endpoint.interfaces.iheaders_extractor.IHeadersExtractor;
import info.smart_tools.smartactors.http_endpoint.interfaces.iheaders_extractor.exceptions.HeadersSetterException;
import info.smart_tools.smartactors.http_endpoint.interfaces.iresponse_status_extractor.IResponseStatusExtractor;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

/**
 * Response sender object for received HTTP request.
 * It sends a response to the request and closes the connection.
 */
public class HttpResponseSender implements IResponseSender {
    /**
     * Constructor for http response sender
     */
    private final ICookiesSetter cookiesSetter;
    private final IHeadersExtractor headersSetter;
    private final IResponseStatusExtractor responseStatusSetter;
    private final String name;

    /**
     * Constructor for response sender
     *
     * @param name Name of the response sender
     * @throws ResolutionException if dependencies did not register
     */
    public HttpResponseSender(final String name) throws ResolutionException {
        this.name = name;
        String keyCookiesSetter = IOC.resolve(Keys.getKeyByName("key_for_cookies_extractor"));
        cookiesSetter = IOC.resolve(Keys.getKeyByName(ICookiesSetter.class.getCanonicalName()), keyCookiesSetter,
                name);
        headersSetter = IOC.resolve(Keys.getKeyByName(IHeadersExtractor.class.getCanonicalName()),
                IOC.resolve(Keys.getKeyByName("key_for_headers_extractor")),
                name);
        responseStatusSetter = IOC.resolve(Keys.getKeyByName(IResponseStatusExtractor.class.getCanonicalName()),
                IOC.resolve(Keys.getKeyByName("key_for_response_status_setter")),
                name);
    }

    @Override
    public void send(final IResponse responseObject, final IObject environment,
                     final IChannelHandler ctx) throws ResponseSendingException {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, getResponseStatus(environment),
                Unpooled.wrappedBuffer(responseObject.getContent()));
        try {
            headersSetter.set(response, environment);
        } catch (HeadersSetterException e) {
            throw new ResponseSendingException("Failed to set headers to response", e);
        }
        try {
            cookiesSetter.set(response, environment);
        } catch (CookieSettingException e) {
            throw new ResponseSendingException("Failed to set cookies to response", e);
        }
        ctx.send(response);
    }

    private HttpResponseStatus getResponseStatus(final IObject environment) {
        Integer responseStatusCode = responseStatusSetter.extract(environment);
        if (null != responseStatusCode) {
            return HttpResponseStatus.valueOf(responseStatusCode);
        }
        return HttpResponseStatus.OK;
    }
}
