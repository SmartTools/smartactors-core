package info.smart_tools.smartactors.http_endpoint.http_request_maker;

import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.endpoint.irequest_maker.IRequestMaker;
import info.smart_tools.smartactors.endpoint.irequest_maker.exception.RequestMakerException;
import info.smart_tools.smartactors.http_endpoint.message_to_bytes_mapper.MessageToBytesMapper;
import info.smart_tools.smartactors.iobject.ds_object.DSObject;
import info.smart_tools.smartactors.iobject.field_name.FieldName;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.ioc.resolve_by_name_ioc_strategy.ResolveByNameIocStrategy;
import info.smart_tools.smartactors.ioc.strategy_container.StrategyContainer;
import info.smart_tools.smartactors.scope.iscope.IScope;
import info.smart_tools.smartactors.scope.scope_provider.ScopeProvider;
import io.netty.handler.codec.http.*;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpRequestMakerTest {

    private static final String EMPTY_PATH = "";

    private IRequestMaker<FullHttpRequest> requestMaker;

    private final URI uri;
    private final String contentBody;

    private IObject content;

    private IFieldName nameFN;
    private IFieldName valueFN;
    private IFieldName requestUriFN;
    private IFieldName requestMethodFN;
    private IFieldName requestVersionFN;
    private IFieldName requestContentFN;
    private IFieldName requestHeadersFN;
    private IFieldName requestCookiesFN;
    private IFieldName cookiesEncoderFN;

    public HttpRequestMakerTest() throws URISyntaxException {
        this.uri = new URI("http://example.com?p1=1&p2=2");
        this.contentBody = "{\"name-1\":\"value-1\", \"name-2\":2}";
    }


    @Before
    public void setUp() throws Exception {

        ScopeProvider.subscribeOnCreationNewScope(
                scope -> {
                    try {
                        scope.setValue(IOC.getIocKey(), new StrategyContainer());
                    } catch (Exception e) {
                        throw new Error(e);
                    }
                }
        );

        Object keyOfMainScope = ScopeProvider.createScope(null);
        IScope mainScope = ScopeProvider.getScope(keyOfMainScope);
        ScopeProvider.setCurrentScope(mainScope);

        IOC.register(
                IOC.getKeyForKeyByNameStrategy(),
                new ResolveByNameIocStrategy()
        );

        IKey fieldNameKey = Keys.getKeyByName(IFieldName.class.getCanonicalName());
        IOC.register(
                fieldNameKey,
                new ApplyFunctionToArgumentsStrategy((args) ->
                        new FieldName((String) args[0])
                )
        );

        this.nameFN =            IOC.resolve(fieldNameKey, "name");
        this.valueFN =           IOC.resolve(fieldNameKey, "value");
        this.requestUriFN =      IOC.resolve(fieldNameKey, "uri");
        this.requestMethodFN =   IOC.resolve(fieldNameKey, "method");
        this.requestVersionFN =  IOC.resolve(fieldNameKey, "version");
        this.requestContentFN =  IOC.resolve(fieldNameKey, "content");
        this.requestHeadersFN =  IOC.resolve(fieldNameKey, "headers");
        this.requestCookiesFN =  IOC.resolve(fieldNameKey, "cookie");
        this.cookiesEncoderFN =  IOC.resolve(fieldNameKey, "cookieEncoder");

        this.requestMaker = new HttpRequestMaker();

        this.content = new DSObject(contentBody);

        MessageToBytesMapper mapper = mock(MessageToBytesMapper.class);
        when(mapper.serialize(eq(content))).thenReturn(contentBody.getBytes());

        IStrategy strategy = mock(IStrategy.class);
        when(strategy.resolve()).thenReturn(mapper);

        IOC.register(
                Keys.getKeyByName(MessageToBytesMapper.class.getCanonicalName()),
                strategy
        );
    }


    @Test
    public void should_MakeHttpRequest_WithContent_UseDefaultVersionAndCookieEncoder() throws Exception {
        List<IObject> requestHeaders = this.getCustomHeaders();
        List<IObject> requestCookies = this.getCustomCookies();

        IObject request = new DSObject();
        request.setValue(requestMethodFN, HttpMethod.POST.name());
        request.setValue(requestUriFN, uri.toASCIIString());
        request.setValue(requestHeadersFN, requestHeaders);
        request.setValue(requestCookiesFN, requestCookies);
        request.setValue(requestContentFN, content);

        FullHttpRequest httpRequest = requestMaker.make(request);

        assertEquals(
                "Invalid request method",
                HttpMethod.POST,
                httpRequest.method()
        );

        assertEquals(
                "Invalid request URI",
                this.uri.toURL().getFile(),
                httpRequest.uri()
        );

        List<IObject> expectedHeaders = new ArrayList<>(requestHeaders);
        expectedHeaders.addAll(this.getRequiredCommonHeaders());
        expectedHeaders.addAll(this.getRequiredContentHeaders());

        for (IObject expectedHeader: expectedHeaders) {
            String expectedValue = expectedHeader.getValue(valueFN).toString();
            String actualValue = httpRequest.headers().get(expectedHeader.getValue(nameFN).toString());

            assertEquals("Invalid request headers", expectedValue, actualValue);
        }

        List<String> expectedCookies = new ArrayList<String>() {{
            add("cookie-1=1");
            add("cookie-2=2.0");
        }};
        List<String> actualCookies = httpRequest.headers().getAll("Cookie");
        assertEquals("Invalid request cookies", expectedCookies, actualCookies);

        assertArrayEquals(
                "Invalid request content",
                contentBody.getBytes(),
                httpRequest.content().array()
        );
    }

    @Test
    public void should_MakeHttpRequest_WithPort_ParseUriAndHostCorrectly() throws Exception {
        List<IObject> requestHeaders = this.getCustomHeaders();
        List<IObject> requestCookies = this.getCustomCookies();

        IObject request = new DSObject();
        request.setValue(requestMethodFN, HttpMethod.GET.name());
        request.setValue(requestUriFN, "https://test.com:8080/some/location");
        request.setValue(requestHeadersFN, requestHeaders);
        request.setValue(requestCookiesFN, requestCookies);

        FullHttpRequest httpRequest = requestMaker.make(request);

        String host = httpRequest.headers().get("Host");
        assertEquals("Incorrect HOST header value", "test.com:8080", host);
        assertEquals("Incorrect URI value", "/some/location", httpRequest.uri());
    }

    @Test
    public void should_MakeHttpRequest_WithPortAndRootLocation_ParseUriAndHostCorrectly() throws Exception {
        List<IObject> requestHeaders = this.getCustomHeaders();
        List<IObject> requestCookies = this.getCustomCookies();

        IObject request = new DSObject();
        request.setValue(requestMethodFN, HttpMethod.GET.name());
        request.setValue(requestUriFN, "https://test.com:8080");
        request.setValue(requestHeadersFN, requestHeaders);
        request.setValue(requestCookiesFN, requestCookies);

        FullHttpRequest httpRequest = requestMaker.make(request);

        String host = httpRequest.headers().get("Host");
        assertEquals("Incorrect HOST header value", "test.com:8080", host);
        assertEquals("Incorrect URI value", EMPTY_PATH, httpRequest.uri());
    }

    @Test
    public void should_MakeHttpRequest_WithoutContent_UseDefaultVersionAndCookieEncoder() throws Exception {
        List<IObject> requestHeaders = this.getCustomHeaders();
        List<IObject> requestCookies = this.getCustomCookies();

        IObject request = new DSObject();
        request.setValue(requestMethodFN, HttpMethod.GET.name());
        request.setValue(requestUriFN, uri.toASCIIString());
        request.setValue(requestHeadersFN, requestHeaders);
        request.setValue(requestCookiesFN, requestCookies);

        FullHttpRequest httpRequest = requestMaker.make(request);

        assertEquals(
                "Invalid request method",
                HttpMethod.GET,
                httpRequest.method()
        );

        assertEquals(
                "Invalid request version",
                HttpVersion.HTTP_1_1,
                httpRequest.protocolVersion()
        );

        assertEquals(
                "Invalid request URI",
                this.uri.toURL().getFile(),
                httpRequest.uri()
        );

        List<IObject> expectedHeaders = new ArrayList<>(requestHeaders);
        expectedHeaders.addAll(this.getRequiredCommonHeaders());
        expectedHeaders.add(createKeyValue(HttpHeaderNames.CONTENT_LENGTH.toString(), 0));

        for (IObject header: expectedHeaders) {
            String expectedValue = header.getValue(valueFN).toString();
            String actualValue = httpRequest.headers().get(header.getValue(nameFN).toString());

            assertEquals("Invalid request headers", expectedValue, actualValue);
        }

        List<String> expectedCookies = new ArrayList<String>() {{
            add("cookie-1=1");
            add("cookie-2=2.0");
        }};
        List<String> actualCookies = httpRequest.headers().getAll("Cookie");
        assertEquals("Invalid request cookies", expectedCookies, actualCookies);
    }

    @Test
    public void should_MakeHttpRequest_WithContent_UseSpecificVersionAndCookieEncoder() throws Exception {
        List<IObject> requestHeaders = this.getCustomHeaders();
        List<IObject> requestCookies = this.getCustomCookies();

        IObject request = new DSObject();
        request.setValue(requestMethodFN, HttpMethod.PUT.name());
        request.setValue(requestVersionFN, "HTTP/1.0");
        request.setValue(requestUriFN, uri.toASCIIString());
        request.setValue(requestHeadersFN, requestHeaders);
        request.setValue(requestCookiesFN, requestCookies);
        request.setValue(cookiesEncoderFN, "lax");
        request.setValue(requestContentFN, content);

        FullHttpRequest httpRequest = requestMaker.make(request);

        assertEquals(
                "Invalid request method",
                HttpMethod.PUT,
                httpRequest.method()
        );

        assertEquals(
                "Invalid request version",
                HttpVersion.HTTP_1_0,
                httpRequest.protocolVersion()
        );

        assertEquals(
                "Invalid request URI",
                this.uri.toURL().getFile(),
                httpRequest.uri()
        );

        List<IObject> expectedHeaders = new ArrayList<>(requestHeaders);
        expectedHeaders.addAll(this.getRequiredCommonHeaders());
        expectedHeaders.addAll(this.getRequiredContentHeaders());

        for (IObject header: expectedHeaders) {
            String expectedValue = header.getValue(valueFN).toString();
            String actualValue = httpRequest.headers().get(header.getValue(nameFN).toString());

            assertEquals("Invalid request headers", expectedValue, actualValue);
        }

        List<String> expectedCookies = new ArrayList<String>() {{
            add("cookie-1=1");
            add("cookie-1=1");
            add("cookie-2=2.0");
        }};
        List<String> actualCookies = httpRequest.headers().getAll("Cookie");
        assertEquals("Invalid request cookies", expectedCookies, actualCookies);

        assertArrayEquals(
                "Invalid request content",
                contentBody.getBytes(),
                httpRequest.content().array()
        );
    }

    @Test
    public void should_MakeHttpRequest_WithContent_WithoutCustomHeadersAndCookies() throws Exception {
        IObject request = new DSObject();
        request.setValue(requestMethodFN, HttpMethod.PUT.name());
        request.setValue(requestUriFN, uri.toASCIIString());
        request.setValue(requestContentFN, content);

        FullHttpRequest httpRequest = requestMaker.make(request);

        assertEquals(
                "Invalid request method",
                HttpMethod.PUT,
                httpRequest.method()
        );

        assertEquals(
                "Invalid request version",
                HttpVersion.HTTP_1_1,
                httpRequest.protocolVersion()
        );

        assertEquals(
                "Invalid request URI",
                this.uri.toURL().getFile(),
                httpRequest.uri()
        );

        List<IObject> expectedHeaders = new ArrayList<>();
        expectedHeaders.addAll(this.getRequiredCommonHeaders());
        expectedHeaders.addAll(this.getRequiredContentHeaders());

        for (IObject header: expectedHeaders) {
            String expectedValue = header.getValue(valueFN).toString();
            String actualValue = httpRequest.headers().get(header.getValue(nameFN).toString());

            assertEquals("Invalid request headers", expectedValue, actualValue);
        }

        assertArrayEquals(
                "Invalid request content",
                contentBody.getBytes(),
                httpRequest.content().array()
        );
    }

    @Test(expected = RequestMakerException.class)
    public void should_ThrowException_RequestIsNull() throws Exception {
        requestMaker.make(null);
    }

    @Test(expected = RequestMakerException.class)
    public void should_ThrowException_RequestMethodIsNull() throws Exception {
        IObject request = new DSObject();
        request.setValue(requestMethodFN, null);
        request.setValue(requestUriFN, uri.toASCIIString());

        requestMaker.make(request);
    }

    @Test(expected = RequestMakerException.class)
    public void should_ThrowException_InvalidRequestMethod() throws Exception {
        IObject request = new DSObject();
        request.setValue(requestMethodFN, "");
        request.setValue(requestUriFN, uri.toASCIIString());
        request.setValue(requestContentFN, content);

        requestMaker.make(request);
    }

    @Test(expected = RequestMakerException.class)
    public void should_ThrowException_InvalidRequestVersion() throws Exception {
        IObject request = new DSObject();
        request.setValue(requestMethodFN, HttpMethod.GET);
        request.setValue(requestVersionFN, "InvalidVersion");

        requestMaker.make(request);
    }

    @Test(expected = RequestMakerException.class)
    public void should_ThrowException_RequestURIIsNull() throws Exception {
        IObject request = new DSObject();
        request.setValue(requestMethodFN, HttpMethod.GET);
        request.setValue(requestUriFN, null);

        requestMaker.make(request);
    }

    @Test(expected = RequestMakerException.class)
    public void should_ThrowException_InvalidRequestURI() throws Exception {
        IObject request = new DSObject();
        request.setValue(requestMethodFN, HttpMethod.GET);
        request.setValue(requestUriFN, "InvalidUri");

        requestMaker.make(request);
    }

    @Test(expected = RequestMakerException.class)
    public void should_ThrowException_InvalidRequestHeaders() throws Exception {
        List<IObject> requestHeaders = this.getCustomHeaders();
        requestHeaders.add(this.createKeyValue("", "emptyHeader"));

        IObject request = new DSObject();
        request.setValue(requestMethodFN, HttpMethod.GET);
        request.setValue(requestUriFN, uri.toASCIIString());
        request.setValue(requestHeadersFN, requestHeaders);

        requestMaker.make(request);
    }

    @Test(expected = RequestMakerException.class)
    public void should_ThrowException_InvalidRequestCookies() throws Exception {
        List<IObject> requestCookies = this.getCustomCookies();
        requestCookies.add(this.createKeyValue("", "emptyCookie"));

        IObject request = new DSObject();
        request.setValue(requestMethodFN, HttpMethod.GET);
        request.setValue(requestUriFN, uri.toASCIIString());
        request.setValue(requestCookiesFN, requestCookies);

        requestMaker.make(request);
    }


    private List<IObject> getCustomHeaders() throws Exception {
        return new ArrayList<IObject>() {{
            add(createKeyValue("header-1", "1"));
            add(createKeyValue("header-1", 1));
            add(createKeyValue("header-2", 2.0));
        }};
    }

    private List<IObject> getRequiredCommonHeaders() throws Exception {
        return new ArrayList<IObject>() {{
           add(createKeyValue(HttpHeaderNames.HOST.toString(), uri.getAuthority()));
           add(createKeyValue(HttpHeaderNames.CONNECTION.toString(), HttpHeaderValues.CLOSE.toString()));
        }};
    }

    private List<IObject> getRequiredContentHeaders() throws Exception {
        return new ArrayList<IObject>() {{
            add(createKeyValue(HttpHeaderNames.CONTENT_LENGTH.toString(), contentBody.length()));
            add(createKeyValue(HttpHeaderNames.CONTENT_TYPE.toString(), "application/json"));
        }};
    }

    private List<IObject> getCustomCookies() throws Exception {
        return new ArrayList<IObject>() {{
            add(createKeyValue("cookie-1", 1));
            add(createKeyValue("cookie-1", "1"));
            add(createKeyValue("cookie-2", 2.0));
        }};
    }

    private IObject createKeyValue(String name, Object value) throws Exception {
        IObject keyValue = new DSObject();
        keyValue.setValue(nameFN, name);
        keyValue.setValue(valueFN, value);

        return keyValue;
    }
}
