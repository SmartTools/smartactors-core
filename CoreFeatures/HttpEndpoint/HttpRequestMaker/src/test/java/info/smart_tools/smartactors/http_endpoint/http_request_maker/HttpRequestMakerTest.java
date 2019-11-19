package info.smart_tools.smartactors.http_endpoint.http_request_maker;

import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.endpoint.irequest_maker.IRequestMaker;
import info.smart_tools.smartactors.endpoint.irequest_maker.exception.RequestMakerException;
import info.smart_tools.smartactors.feature_loading_system.bootstrap.Bootstrap;
import info.smart_tools.smartactors.field_plugins.ifield_plugin.IFieldPlugin;
import info.smart_tools.smartactors.http_endpoint.message_to_bytes_mapper.MessageToBytesMapper;
import info.smart_tools.smartactors.iobject.ds_object.DSObject;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject_plugins.dsobject_plugin.PluginDSObject;
import info.smart_tools.smartactors.iobject_plugins.ifieldname_plugin.IFieldNamePlugin;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.ioc_plugins.ioc_keys_plugin.PluginIOCKeys;
import info.smart_tools.smartactors.ioc_plugins.ioc_simple_container_plugin.PluginIOCSimpleContainer;
import io.netty.handler.codec.http.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class HttpRequestMakerTest {
    private IRequestMaker<FullHttpRequest> requestMaker;

    private final URI uri;
    private final String contentBody;

    private IFieldName nameFN;
    private IFieldName valueFN;
    private IFieldName requestUriFN;
    private IFieldName requestMethodFN;
    private IFieldName requestVersionFN;
    private IFieldName requestContentFN;
    private IFieldName requestHeadersFN;
    private IFieldName requestCookiesFN;
    private IFieldName cookiesEncoderFN;

    public HttpRequestMakerTest() throws Exception {
        this.requestMaker = new HttpRequestMaker();
        this.uri = new URI("http://example.com:8080/?p1=1&p2=2");
        this.contentBody = "{\"name-1\":\"value-1\",\"name-2\":2}";
    }

    @BeforeClass
    public static void initScope() throws Exception {
        Bootstrap bootstrap = new Bootstrap();
        new PluginIOCSimpleContainer(bootstrap).load();
        new PluginIOCKeys(bootstrap).load();
        new IFieldNamePlugin(bootstrap).load();
        new IFieldPlugin(bootstrap).load();
        new PluginDSObject(bootstrap).load();
        bootstrap.start();

        MessageToBytesMapper mapper = mock(MessageToBytesMapper.class);
        doAnswer(invocation -> {
            IObject argument = (IObject) invocation.getArguments()[0];
            return ((String) argument.serialize()).getBytes();
        }).when(mapper).serialize(any(IObject.class));

        IStrategy strategy = mock(IStrategy.class);
        when(strategy.resolve()).thenReturn(mapper);

        IOC.register(
                Keys.getKeyByName(MessageToBytesMapper.class.getCanonicalName()),
                strategy
        );
    }

    @Before
    public void setUp() throws Exception {
        IKey fieldNameKey = Keys.getKeyByName(IFieldName.class.getCanonicalName());
        this.nameFN = IOC.resolve(fieldNameKey, "name");
        this.valueFN = IOC.resolve(fieldNameKey, "value");
        this.requestUriFN = IOC.resolve(fieldNameKey, "uri");
        this.requestMethodFN = IOC.resolve(fieldNameKey, "method");
        this.requestVersionFN = IOC.resolve(fieldNameKey, "version");
        this.requestContentFN = IOC.resolve(fieldNameKey, "content");
        this.requestHeadersFN = IOC.resolve(fieldNameKey, "headers");
        this.requestCookiesFN = IOC.resolve(fieldNameKey, "cookie");
        this.cookiesEncoderFN = IOC.resolve(fieldNameKey, "cookieEncoder");
    }

    private IObject getDefaultRequest() throws Exception {
        IObject request = IOC.resolve(
                Keys.getKeyByName(IObject.class.getCanonicalName())
        );
        request.setValue(requestMethodFN, HttpMethod.GET.name());
        request.setValue(requestUriFN, uri.toASCIIString());
        IObject content = new DSObject(contentBody);
        request.setValue(requestContentFN, content);
        return request;
    }

    @Test
    public void shouldCreateRequestWithDefaultTestData() throws Exception {
        IObject request = getDefaultRequest();
        requestMaker.make(request);
    }

    @Test(expected = RequestMakerException.class)
    public void shouldThrowExceptionWhenRequestIsNull() throws Exception {
        requestMaker.make(null);
    }

    @Test
    public void shouldBuildRequestWithCorrectRequestMethod() throws Exception {
        IObject request = getDefaultRequest();

        FullHttpRequest httpRequest = requestMaker.make(request);
        assertEquals(
                "Invalid request method",
                HttpMethod.GET,
                httpRequest.method()
        );

        request.setValue(requestMethodFN, HttpMethod.POST.name());
        httpRequest = requestMaker.make(request);
        assertEquals(
                "Invalid request method",
                HttpMethod.POST,
                httpRequest.method()
        );
    }

    @Test(expected = RequestMakerException.class)
    public void shouldThrowExceptionWhenRequestMethodIsNull() throws Exception {
        IObject request = getDefaultRequest();
        request.setValue(requestMethodFN, null);
        requestMaker.make(request);
    }

    @Test(expected = RequestMakerException.class)
    public void shouldThrowExceptionWhenRequestMethodIsInvalid() throws Exception {
        IObject request = getDefaultRequest();
        request.setValue(requestMethodFN, "");
        requestMaker.make(request);
    }

    @Test
    public void shouldBuildHostHeaderAndURIWhenURLHasNoPort() throws Exception {
        IObject request = getDefaultRequest();
        request.setValue(requestUriFN, "http://example.com/some/location");

        FullHttpRequest httpRequest = requestMaker.make(request);
        String host = httpRequest.headers().get("Host");
        assertEquals("Incorrect HOST header value", "example.com", host);
        assertEquals("Incorrect URI value", "/some/location", httpRequest.uri());
    }

    @Test
    public void shouldBuildHostHeaderAndURIWhenURLHasQueryString() throws Exception {
        IObject request = getDefaultRequest();
        request.setValue(requestUriFN, "http://example.com/test/location?p1=val1&p2=val2");

        FullHttpRequest httpRequest = requestMaker.make(request);
        String host = httpRequest.headers().get("Host");
        assertEquals("Incorrect HOST header value", "example.com", host);
        assertEquals("Incorrect URI value", "/test/location?p1=val1&p2=val2", httpRequest.uri());
    }

    @Test
    public void shouldBuildHostHeaderAndURIWhenURLHasPort() throws Exception {
        IObject request = getDefaultRequest();
        request.setValue(requestUriFN, "http://example.com:8080/test/location");

        FullHttpRequest httpRequest = requestMaker.make(request);
        String host = httpRequest.headers().get("Host");
        assertEquals("Incorrect HOST header value", "example.com:8080", host);
        assertEquals("Incorrect URI value", "/test/location", httpRequest.uri());
    }

    @Test(expected = RequestMakerException.class)
    public void shouldThrowExceptionWhenURIHasIncorrectFormat() throws Exception {
        IObject request = getDefaultRequest();
        request.setValue(requestUriFN, "IncorrectUri");
        requestMaker.make(request);
    }

    @Test(expected = RequestMakerException.class)
    public void shouldThrowExceptionWhenURIIsNull() throws Exception {
        IObject request = getDefaultRequest();
        request.setValue(requestUriFN, null);
        requestMaker.make(request);
    }

    @Test
    public void shouldUseDefaultProtocolVersionWhenItIsNull() throws Exception {
        IObject request = getDefaultRequest();
        request.setValue(requestVersionFN, null);

        FullHttpRequest httpRequest = requestMaker.make(request);
        assertEquals(
                "Incorrect default protocol version",
                HttpVersion.HTTP_1_1,
                httpRequest.protocolVersion()
        );
    }

    @Test
    public void shouldUseCustomProtocolVersionWhenItIsPassed() throws Exception {
        IObject request = getDefaultRequest();
        request.setValue(requestVersionFN, HttpVersion.HTTP_1_0.toString());

        FullHttpRequest httpRequest = requestMaker.make(request);
        assertEquals(
                "Incorrect protocol version",
                HttpVersion.HTTP_1_0,
                httpRequest.protocolVersion()
        );
    }

    @Test
    public void shouldAddDefaultHeadersWhenHeadersFieldIsNull() throws Exception {
        IObject request = getDefaultRequest();
        request.setValue(requestHeadersFN, null);
        FullHttpRequest httpRequest = requestMaker.make(request);

        List<IObject> expectedHeaders = new ArrayList<>();
        expectedHeaders.addAll(this.getRequiredCommonHeaders());
        expectedHeaders.addAll(this.getRequiredContentHeaders());

        assertEquals(
                "Incorrect headers amount",
                expectedHeaders.size(),
                httpRequest.headers().size()
        );

        for (IObject expectedHeader : expectedHeaders) {
            String headerName = String.valueOf(expectedHeader.getValue(nameFN));
            String expectedValue = String.valueOf(expectedHeader.getValue(valueFN));
            String actualValue = httpRequest.headers().get(headerName);

            assertEquals(
                    String.format("Invalid %s header value", headerName),
                    expectedValue,
                    actualValue
            );
        }
    }

    @Test
    public void shouldAddDefaultHeadersWhenHeadersFieldIsEmptyIObject() throws Exception {
        IObject request = getDefaultRequest();
        ArrayList<IObject> customHeaders = new ArrayList<>();
        request.setValue(requestHeadersFN, customHeaders);

        FullHttpRequest httpRequest = requestMaker.make(request);
        List<IObject> expectedHeaders = new ArrayList<>();
        expectedHeaders.addAll(this.getRequiredCommonHeaders());
        expectedHeaders.addAll(this.getRequiredContentHeaders());

        assertEquals(
                "Incorrect headers amount",
                expectedHeaders.size(),
                httpRequest.headers().size()
        );

        for (IObject expectedHeader : expectedHeaders) {
            String headerName = String.valueOf(expectedHeader.getValue(nameFN));
            String expectedValue = String.valueOf(expectedHeader.getValue(valueFN));
            String actualValue = httpRequest.headers().get(headerName);

            assertEquals(
                    String.format("Invalid %s header value", headerName),
                    expectedValue,
                    actualValue
            );
        }
    }

    @Test
    public void shouldAddDefaultHeadersWhenCustomHeadersAreSpecified() throws Exception {
        IObject request = getDefaultRequest();
        List<IObject> customHeaders = getCustomHeaders();
        request.setValue(requestHeadersFN, customHeaders);

        FullHttpRequest httpRequest = requestMaker.make(request);
        List<IObject> expectedHeaders = new ArrayList<>();
        expectedHeaders.addAll(this.getRequiredCommonHeaders());
        expectedHeaders.addAll(this.getRequiredContentHeaders());
        expectedHeaders.addAll(customHeaders);

        assertEquals(
                "Incorrect headers amount",
                expectedHeaders.size(),
                httpRequest.headers().size()
        );

        for (IObject expectedHeader : expectedHeaders) {
            String headerName = String.valueOf(expectedHeader.getValue(nameFN));
            String expectedValue = String.valueOf(expectedHeader.getValue(valueFN));
            String actualValue = httpRequest.headers().get(headerName);

            assertEquals(
                    String.format("Invalid %s header value", headerName),
                    expectedValue,
                    actualValue
            );
        }
    }

    @Test(expected = RequestMakerException.class)
    public void shouldThrowExceptionWhenRequestHeadersAreIncorrect() throws Exception {
        List<IObject> requestHeaders = this.getCustomHeaders();
        requestHeaders.add(this.createKeyValue("", "emptyHeader"));
        IObject request = getDefaultRequest();
        request.setValue(requestHeadersFN, requestHeaders);
        requestMaker.make(request);
    }

    @Test
    public void shouldMakeHttpRequestWithNullContent() throws Exception {
        IObject request = getDefaultRequest();
        request.setValue(requestContentFN, null);
        FullHttpRequest httpRequest = requestMaker.make(request);
        assertArrayEquals(
                "Invalid request content",
                new byte[]{},
                httpRequest.content().array()
        );
    }

    @Test
    public void shouldMakeHttpRequestWithEmptyIObjectContent() throws Exception {
        IObject request = getDefaultRequest();
        IObject content = IOC.resolve(Keys.getKeyByName(IObject.class.getCanonicalName()));
        request.setValue(requestContentFN, content);
        FullHttpRequest httpRequest = requestMaker.make(request);
        assertArrayEquals(
                "Invalid request content",
                ((String) content.serialize()).getBytes(),
                httpRequest.content().array()
        );
    }

    @Test
    public void shouldMakeHttpRequestWithNonEmptyIObjectContent() throws Exception {
        IObject request = getDefaultRequest();
        IObject content = IOC.resolve(
                Keys.getKeyByName(IObject.class.getCanonicalName()),
                "{ \"field\": \"value\" }"
        );
        request.setValue(requestContentFN, content);
        FullHttpRequest httpRequest = requestMaker.make(request);
        assertArrayEquals(
                "Invalid request content",
                ((String) content.serialize()).getBytes(),
                httpRequest.content().array()
        );
    }

    @Test
    public void shouldMakeHttpRequestWithDefaultCookieEncoder() throws Exception {
        List<IObject> requestCookies = this.getCustomCookies();

        IObject request = getDefaultRequest();
        request.setValue(requestCookiesFN, requestCookies);

        FullHttpRequest httpRequest = requestMaker.make(request);

        List<String> expectedCookies = new ArrayList<String>() {{
            add("cookie-1=1");
            add("cookie-2=2.0");
        }};
        List<String> actualCookies = httpRequest.headers().getAll("Cookie");
        assertEquals("Invalid request cookies", expectedCookies, actualCookies);
    }

    @Test
    public void shouldMakeHttpRequestWithContentCookieEncoder() throws Exception {
        List<IObject> requestCookies = this.getCustomCookies();

        IObject request = getDefaultRequest();
        request.setValue(requestCookiesFN, requestCookies);
        request.setValue(cookiesEncoderFN, "lax");

        FullHttpRequest httpRequest = requestMaker.make(request);

        List<String> expectedCookies = new ArrayList<String>() {{
            add("cookie-1=1");
            add("cookie-1=1");
            add("cookie-2=2.0");
        }};
        List<String> actualCookies = httpRequest.headers().getAll("Cookie");
        assertEquals("Invalid request cookies", expectedCookies, actualCookies);
    }

    @Test(expected = RequestMakerException.class)
    public void shouldThrowExceptionWhenRequestCookiesAreInvalid() throws Exception {
        List<IObject> requestCookies = this.getCustomCookies();
        requestCookies.add(this.createKeyValue("", "emptyCookie"));
        IObject request = getDefaultRequest();
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
