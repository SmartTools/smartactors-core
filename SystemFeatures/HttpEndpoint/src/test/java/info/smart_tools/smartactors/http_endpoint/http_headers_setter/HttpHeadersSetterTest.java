package info.smart_tools.smartactors.http_endpoint.http_headers_setter;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.helpers.IOCInitializer.IOCInitializer;
import info.smart_tools.smartactors.http_endpoint.interfaces.iheaders_extractor.exceptions.HeadersSetterException;
import info.smart_tools.smartactors.iobject.ds_object.DSObject;
import info.smart_tools.smartactors.iobject.ifield.IField;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpHeadersSetterTest extends IOCInitializer {
    IField field;

    @Override
    protected void registry(String... strategyNames) throws Exception {
        registryStrategies("ifieldname strategy", "iobject strategy");
    }

    @Override
    protected void registerMocks() throws Exception {
        field = mock(IField.class);
        IKey keyIField = Keys.getKeyByName(IField.class.getCanonicalName());
        IOC.register(
                keyIField,
                new SingletonStrategy(field)
        );

    }

    @Test
    public void testSettingHeaders() throws InvalidArgumentException, ReadValueException, HeadersSetterException {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        HttpHeadersExtractor headersSetter = new HttpHeadersExtractor();
        IObject environment = new DSObject("{\n" +
                "  \"context\": {\n" +
                "    \"headers\": [\n" +
                "      {\n" +
                "        \"name\": \"foo\", " +
                "        \"value\": \"bar\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"name\": \"hello\",  " +
                "        \"value\": \"world\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}");
        IObject context = new DSObject(
                " {" +
                        "    \"headers\": [\n" +
                        "      {\n" +
                        "        \"name\": \"foo\", " +
                        "        \"value\": \"bar\"\n" +
                        "      },\n" +
                        "      {\n" +
                        "        \"name\": \"hello\",  " +
                        "        \"value\": \"world\"\n" +
                        "      }\n" +
                        "    ]\n" +
                        "  }\n");
        IObject header1 = new DSObject("{\n" +
                "        \"name\": \"foo\", " +
                "        \"value\": \"bar\"\n" +
                "      }");
        IObject header2 = new DSObject("{\n" +
                "        \"name\": \"hello\", " +
                "        \"value\": \"world\"\n" +
                "      }");
        List<String> headersGoodString = new ArrayList<>(2);
        List<IObject> headers = new ArrayList<>(2);
        headers.add(header1);
        headers.add(header2);
        when(field.in(environment, IObject.class)).thenReturn(context);
        when(field.in(context, List.class)).thenReturn(headers);
        headersGoodString.add("foo=bar");
        headersGoodString.add("hello=world");

        headersSetter.set(response, environment);
        assertEquals("bar", response.headers().get("foo"));
        assertEquals("world", response.headers().get("hello"));
    }

    @Test
    public void testSettingEmptyHeaders() throws InvalidArgumentException, ReadValueException, HeadersSetterException {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        HttpHeadersExtractor headersSetter = new HttpHeadersExtractor();
        IObject environment = new DSObject("{\n" +
                "  \"context\": {\n" +
                "    \"headers\": [" +
                "    ]\n" +
                "  }\n" +
                "}");
        IObject context = new DSObject(
                " {" +
                        "    \"headers\": [\n" +
                        "    ]\n" +
                        "  }\n");
        List<String> headersGoodString = new ArrayList<>(0);
        List<IObject> headers = new ArrayList<>(2);
        when(field.in(environment, IObject.class)).thenReturn(context);
        when(field.in(context, List.class)).thenReturn(headers);
        headersGoodString.add("foo=bar");
        headersGoodString.add("hello=world");

        headersSetter.set(response, environment);

        assertNull(response.headers().get("foo"));
        assertNull(response.headers().get("hello"));
    }
}
