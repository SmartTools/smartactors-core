package info.smart_tools.smartactors.core.http_response_sender;

import info.smart_tools.smartactors.core.ichannel_handler.IChannelHandler;
import info.smart_tools.smartactors.core.icookies_extractor.ICookiesSetter;
import info.smart_tools.smartactors.core.icookies_extractor.exceptions.CookieSettingException;
import info.smart_tools.smartactors.core.iheaders_extractor.IHeadersSetter;
import info.smart_tools.smartactors.core.iheaders_extractor.exceptions.HeadersSetterException;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.iresponse.IResponse;
import info.smart_tools.smartactors.core.iresponse_sender.IResponseSender;
import info.smart_tools.smartactors.core.iresponse_sender.exceptions.ResponseSendingException;
import info.smart_tools.smartactors.core.iresponse_status_extractor.IResponseStatusExtractor;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
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
    private final IHeadersSetter headersSetter;
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
        cookiesSetter = IOC.resolve(Keys.getOrAdd(ICookiesSetter.class.getCanonicalName()), name);
        headersSetter = IOC.resolve(Keys.getOrAdd(IHeadersSetter.class.getCanonicalName()), name);
        responseStatusSetter = IOC.resolve(Keys.getOrAdd(IResponseStatusExtractor.class.getCanonicalName()), name);
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
