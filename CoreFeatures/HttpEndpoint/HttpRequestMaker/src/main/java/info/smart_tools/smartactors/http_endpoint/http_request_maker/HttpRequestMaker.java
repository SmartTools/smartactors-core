package info.smart_tools.smartactors.http_endpoint.http_request_maker;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.endpoint.interfaces.imessage_mapper.IMessageMapper;
import info.smart_tools.smartactors.endpoint.irequest_maker.IRequestMaker;
import info.smart_tools.smartactors.endpoint.irequest_maker.exception.RequestMakerException;
import info.smart_tools.smartactors.http_endpoint.message_to_bytes_mapper.MessageToBytesMapper;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;


/**
 * {@link IRequestMaker} implementation for {@link FullHttpRequest}
 */
public class HttpRequestMaker implements IRequestMaker<FullHttpRequest> {

// Fields ------------------------------------------------------------------------------------------------------------

    /**
     * Common error message template
     */
    private static final String COMMON_ERROR_MSG = "Failed to create HTTP request: %s";

    /**
     * {@code Strict} encoder that validates that name and value chars are in the valid scope
     * defined in RFC6265, and (for methods that accept multiple cookies) that only
     * one cookie is encoded with any given name. (If multiple cookies have the same
     * name, the last one is the one that is encoded.)
     */
    private static final ServerCookieEncoder STRICT_COOKIE_ENCODER = ServerCookieEncoder.STRICT;
    /**
     * {@code Lax} encoder that doesn't validate name and value, and that allows multiple
     * cookies with the same name
     */
    private static final ServerCookieEncoder LAX_COOKIE_ENCODER = ServerCookieEncoder.LAX;

    /**
     * Field names to getting request parameters from {@link IObject}
     */
    private final IFieldName nameFN;
    private final IFieldName valueFN;
    private final IFieldName requestUriFN;
    private final IFieldName requestMethodFN;
    private final IFieldName requestVersionFN;
    private final IFieldName requestHeadersFN;
    private final IFieldName requestCookiesFN;
    private final IFieldName cookiesEncoderFN;
    private final IFieldName requestContentFN;


    private final Map<String, ServerCookieEncoder> cookieEncoders = new HashMap<String, ServerCookieEncoder>() {{
        put("STRICT", STRICT_COOKIE_ENCODER);
        put("LAX", LAX_COOKIE_ENCODER);
    }};

    /**
     * Default HTTP request version
     */
    private final String defaultVersionName = HttpVersion.HTTP_1_1.toString();

    /**
     * Default cookie encoder
     */
    private final String defaultCookieEncoder = "STRICT";

// Constructors ------------------------------------------------------------------------------------------------------

    public HttpRequestMaker() throws ResolutionException {

        final IKey fieldNameKey = Keys.getKeyByName(IFieldName.class.getCanonicalName());

        this.nameFN =            IOC.resolve(fieldNameKey, "name");
        this.valueFN =           IOC.resolve(fieldNameKey, "value");
        this.requestUriFN =      IOC.resolve(fieldNameKey, "uri");
        this.requestMethodFN =   IOC.resolve(fieldNameKey, "method");
        this.requestVersionFN =  IOC.resolve(fieldNameKey, "version");
        this.requestHeadersFN =  IOC.resolve(fieldNameKey, "headers");
        this.requestCookiesFN =  IOC.resolve(fieldNameKey, "cookie");
        this.cookiesEncoderFN =  IOC.resolve(fieldNameKey, "cookieEncoder");
        this.requestContentFN =  IOC.resolve(fieldNameKey, "content");
    }

// Methods -----------------------------------------------------------------------------------------------------------

    /**
     * Creates {@link FullHttpRequest} use given request parameters
     * <p>Request parameters should presents as {@link IObject}</p>
     * <pre>
     *     {
     *         "uri": "http://example.com",
     *         "method": "POST",
     *         "version": "HTTP/1.1",
     *         "headers": [
     *              {
     *                  "name": "headerName",
     *                  "value": @Value
     *              },
     *              ...
     *         ],
     *         "cookie": [
     *              {
     *                  "name": "cookieName",
     *                  "value": @Value
     *              },
     *              ...
     *         ],
     *         "cookieEncoder": "strict",
     *         "content: {
     *             "name1": @Value,
     *             "name2": @Value,
     *             ...
     *         }
     *     }
     * </pre>
     *
     * <p>{@code uri} -           recipient URI</p>
     * <p>{@code method} -        the request method of HTTP or its derived protocols, such as
     *                            <a href="http://en.wikipedia.org/wiki/Real_Time_Streaming_Protocol">RTSP</a> and
     *                            <a href="http://en.wikipedia.org/wiki/Internet_Content_Adaptation_Protocol">ICAP</a></p>
     * <p>{@code version} -       the version of HTTP or its derived protocols, such as
     *                            <a href="http://en.wikipedia.org/wiki/Real_Time_Streaming_Protocol">RTSP</a> and
     *                            <a href="http://en.wikipedia.org/wiki/Internet_Content_Adaptation_Protocol">ICAP</a></p>
     * <p>{@code headers} -       headers to be set, should presents as {@link List<IObject>}</p>
     * <p>{@code cookie} -        cookies to be set, should presents as {@link List<IObject>}</p>
     * <p>{@code cookieEncoder} - cookie encoder type to be used: {@code strict} or {@code lax}.
     *                            Optional value, default {@code strict}.
     *                            {@code Strict} encoder that validates that name and value chars are in the valid scope
     *                            defined in RFC6265, and (for methods that accept multiple cookies) that only
     *                            one cookie is encoded with any given name. (If multiple cookies have the same
     *                            name, the last one is the one that is encoded.)
     *                            {@code Lax} encoder that doesn't validate name and value, and that allows multiple
     *                            cookies with the same name</p>
     * <p>{@code content} -       request's content
     *
     *
     * @param request Request parameters, presents as {@link IObject}
     *
     * @return {@link FullHttpRequest} object which could be used for send HTTP request
     *
     * @throws RequestMakerException When occur HTTP request creation error
     */
    @Override
    public FullHttpRequest make(final IObject request) throws RequestMakerException {
        if (request == null) {
            throw this.getError("Invalid request(NULL)", null);
        }
        try {
            final URL url =                           this.getRequestURL(request);
            final IObject content =                   this.getRequestContent(request);
            final HttpMethod method =                 this.getRequestMethod(request);
            final HttpVersion version =               this.getRequestVersion(request);
            final HttpHeaders headers =               this.getRequestHeaders(request);
            final List<Cookie> cookies =              this.getRequestCookies(request);
            final ServerCookieEncoder cookieEncoder = this.getCookieEncoder(request);

            return this.createHttpRequest(method, version, url, content, headers, cookies, cookieEncoder);
        } catch (ReadValueException | InvalidArgumentException exc) {
            throw this.getError(exc.getMessage(), exc);
        }
    }


    private HttpMethod getRequestMethod(final IObject request)
            throws RequestMakerException, ReadValueException, InvalidArgumentException {

        String methodName = Optional
                .ofNullable(request.getValue(requestMethodFN))
                .orElseThrow(() -> this.getError("Invalid HTTP method(NULL)", null))
                .toString();
        try {
            return HttpMethod.valueOf(methodName);
        } catch (IllegalArgumentException exc) {
            throw this.getError(
                    String.format("Invalid HTTP method(%s)", methodName),
                    exc
            );
        }
    }

    private HttpVersion getRequestVersion(final IObject request)
            throws RequestMakerException, ReadValueException, InvalidArgumentException {

        String versionName = Optional
                .ofNullable(request.getValue(requestVersionFN))
                .orElse(defaultVersionName)
                .toString()
                .trim();
        try {
            return HttpVersion.valueOf(versionName);
        } catch (IllegalArgumentException exc) {
            throw this.getError(
                    String.format("Unsupported HTTP version(%s)", versionName),
                    exc
            );
        }
    }

    private URL getRequestURL(final IObject request)
            throws RequestMakerException, ReadValueException, InvalidArgumentException {

        String requestUri = Optional
                .ofNullable(request.getValue(requestUriFN))
                .orElseThrow(() -> this.getError("Invalid request URI(NULL)", null))
                .toString();
        try {
            return new URL(requestUri);
        } catch (MalformedURLException exc) {
            throw this.getError(String.format("Invalid request URI(%s)", requestUri), exc);
        }
    }

    private IObject getRequestContent(final IObject request)
            throws RequestMakerException, ReadValueException, InvalidArgumentException {

        try {
            return (IObject) request.getValue(requestContentFN);
        } catch (ClassCastException exc) {
            throw this.getError("Invalid request content", exc);
        }
    }

    @SuppressWarnings("unchecked")
    private HttpHeaders getRequestHeaders(final IObject request)
            throws RequestMakerException, ReadValueException, InvalidArgumentException {

        try {
            final List<IObject> headers = (List<IObject>) request.getValue(requestHeadersFN);
            if (headers == null || headers.isEmpty()) {
                return EmptyHttpHeaders.INSTANCE;
            }

            HttpHeaders httpHeaders = new DefaultHttpHeaders(true);
            for (IObject header: headers) {
                httpHeaders.add(
                        String.valueOf(header.getValue(nameFN)),
                        header.getValue(valueFN)
                );
            }

            return httpHeaders;
        } catch (ClassCastException | IllegalArgumentException exc) {
            throw this.getError("Invalid request headers", exc);
        }
    }

    @SuppressWarnings("unchecked")
    private List<Cookie> getRequestCookies(final IObject request)
            throws RequestMakerException, ReadValueException, InvalidArgumentException {

        try {
            final List<IObject> cookies = (List<IObject>) request.getValue(requestCookiesFN);
            if (cookies == null || cookies.isEmpty()) {
                return Collections.emptyList();
            }

            List<Cookie> httpCookies = new ArrayList<>(cookies.size());
            for (IObject cookie: cookies) {
                String cookieName = Optional
                        .ofNullable(cookie.getValue(nameFN))
                        .orElseThrow(() -> this.getError("Empty cookie name", null))
                        .toString()
                        .trim();

                if (cookieName.isEmpty()) {
                    throw this.getError("Empty cookie name", null);
                }

                String cookieValue = Optional
                        .ofNullable(cookie.getValue(valueFN))
                        .orElse("")
                        .toString();

                httpCookies.add(new DefaultCookie(cookieName, cookieValue));
            }

            return httpCookies;
        } catch (ClassCastException exc) {
            throw this.getError("Invalid request cookies", exc);
        }
    }

    private ServerCookieEncoder getCookieEncoder(final IObject request)
            throws RequestMakerException, ReadValueException, InvalidArgumentException {

        String encoderType = Optional
                .ofNullable(request.getValue(cookiesEncoderFN))
                .orElse(defaultCookieEncoder)
                .toString()
                .toUpperCase()
                .trim();

        return Optional
                .ofNullable(cookieEncoders.get(encoderType))
                .orElseThrow(() -> this.getError(
                        String.format("Unsupported cookie encoder(%s)", encoderType),
                        null
                ));
    }

    private FullHttpRequest createHttpRequest(
            HttpMethod method,
            HttpVersion version,
            URL url,
            final IObject content,
            final HttpHeaders headers,
            final List<Cookie> cookies,
            ServerCookieEncoder cookieEncoder
    ) throws RequestMakerException {
        try {
            FullHttpRequest httpRequest;
            String path = url.getFile();

            if (content == null){
                httpRequest = new DefaultFullHttpRequest(
                        version,
                        method,
                        path
                );
            } else {
                IKey contentMapperKey = Keys.getKeyByName(MessageToBytesMapper.class.getCanonicalName());
                IMessageMapper<byte[]> contentMapper = IOC.resolve(contentMapperKey);

                httpRequest = new DefaultFullHttpRequest(
                        version,
                        method,
                        path,
                        Unpooled.copiedBuffer(contentMapper.serialize(content))
                );
            }

            httpRequest.headers().set(headers);

            if (content != null) {
                httpRequest.headers().set(
                        HttpHeaderNames.CONTENT_LENGTH,
                        httpRequest.content().readableBytes()
                );
                httpRequest.headers().set(
                        HttpHeaderNames.CONTENT_TYPE,
                        "application/json"
                );
            } else {
                httpRequest.headers().set(
                        HttpHeaderNames.CONTENT_LENGTH,
                        0
                );
            }
            httpRequest.headers().set(
                    HttpHeaderNames.HOST,
                    url.getAuthority()
            );
            httpRequest.headers().set(
                    HttpHeaderNames.CONNECTION,
                    HttpHeaderValues.CLOSE
            );
            httpRequest.headers().set(
                    HttpHeaderNames.COOKIE,
                    cookieEncoder.encode(cookies)
            );

            return httpRequest;
        } catch (ResolutionException exc) {
            throw this.getError(exc.getMessage(), exc);
        }
    }

    private RequestMakerException getError(String message, Throwable cause) {
        String errMsg = String.format(COMMON_ERROR_MSG, message);
        return new RequestMakerException(errMsg, cause);
    }
}
