package info.smart_tools.smartactors.strategy.http_headers_setter;

import info.smart_tools.smartactors.core.create_new_instance_strategy.CreateNewInstanceStrategy;
import info.smart_tools.smartactors.core.ds_object.DSObject;
import info.smart_tools.smartactors.core.field_name.FieldName;
import info.smart_tools.smartactors.core.ifield.IField;
import info.smart_tools.smartactors.core.ifield_name.IFieldName;
import info.smart_tools.smartactors.core.iheaders_extractor.exceptions.HeadersSetterException;
import info.smart_tools.smartactors.core.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.iscope.IScope;
import info.smart_tools.smartactors.core.iscope_provider_container.exception.ScopeProviderException;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.core.resolve_by_name_ioc_strategy.ResolveByNameIocStrategy;
import info.smart_tools.smartactors.core.scope_provider.ScopeProvider;
import info.smart_tools.smartactors.core.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.core.strategy_container.StrategyContainer;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpHeadersSetterTest {
    IField field;

    @Before
    public void setUp() throws ScopeProviderException, RegistrationException, ResolutionException, InvalidArgumentException {
        field = mock(IField.class);
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
                IOC.getKeyForKeyStorage(),
                new ResolveByNameIocStrategy()
        );

        IKey keyFieldName = Keys.getOrAdd(IFieldName.class.getCanonicalName());
        IOC.register(
                keyFieldName,
                new CreateNewInstanceStrategy(
                        (args) -> {
                            try {
                                return new FieldName((String) args[0]);
                            } catch (InvalidArgumentException ignored) {
                            }
                            return null;
                        }
                )
        );
        IKey keyIField = Keys.getOrAdd(IField.class.getCanonicalName());
        IOC.register(
                keyIField,
                new SingletonStrategy(field)
        );

    }

    @Test
    public void testSettingHeaders() throws InvalidArgumentException, ReadValueException, HeadersSetterException {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        HttpHeadersSetter headersSetter = new HttpHeadersSetter();
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
        HttpHeadersSetter headersSetter = new HttpHeadersSetter();
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
