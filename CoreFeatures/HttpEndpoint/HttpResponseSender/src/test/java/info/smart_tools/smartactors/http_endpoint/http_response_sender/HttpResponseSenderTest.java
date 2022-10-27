package info.smart_tools.smartactors.http_endpoint.http_response_sender;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.endpoint.interfaces.ichannel_handler.IChannelHandler;
import info.smart_tools.smartactors.endpoint.interfaces.iresponse.IResponse;
import info.smart_tools.smartactors.endpoint.interfaces.iresponse_sender.exceptions.ResponseSendingException;
import info.smart_tools.smartactors.http_endpoint.interfaces.icookies_extractor.ICookiesSetter;
import info.smart_tools.smartactors.http_endpoint.interfaces.icookies_extractor.exceptions.CookieSettingException;
import info.smart_tools.smartactors.http_endpoint.interfaces.iheaders_extractor.IHeadersExtractor;
import info.smart_tools.smartactors.http_endpoint.interfaces.iheaders_extractor.exceptions.HeadersSetterException;
import info.smart_tools.smartactors.http_endpoint.interfaces.iresponse_status_extractor.IResponseStatusExtractor;
import info.smart_tools.smartactors.iobject.ds_object.DSObject;
import info.smart_tools.smartactors.iobject.field_name.FieldName;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.ioc.resolve_by_name_ioc_strategy.ResolveByNameIocStrategy;
import info.smart_tools.smartactors.ioc.strategy_container.StrategyContainer;
import info.smart_tools.smartactors.scope.iscope.IScope;
import info.smart_tools.smartactors.scope.iscope_provider_container.exception.ScopeProviderException;
import info.smart_tools.smartactors.scope.scope_provider.ScopeProvider;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;


public class HttpResponseSenderTest {
    private IChannelHandler ctx;
    private ICookiesSetter cookiesExtractor;
    private IHeadersExtractor headersExtractor;
    private IResponseStatusExtractor responseStatusExtractor;
    private IResponse response;
    private String key = null;

    @Before
    public void setUp() throws ScopeProviderException, RegistrationException, ResolutionException, InvalidArgumentException {
        ctx = mock(IChannelHandler.class);
        cookiesExtractor = mock(ICookiesSetter.class);
        headersExtractor = mock(IHeadersExtractor.class);
        responseStatusExtractor = mock(IResponseStatusExtractor.class);
        response = mock(IResponse.class);

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
        IKey keyFieldName = Keys.getKeyByName(IFieldName.class.getCanonicalName());
        IKey keyIObject = Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject");
        IKey keyCookiesExtractor = Keys.getKeyByName(ICookiesSetter.class.getCanonicalName());
        IKey keyHeadersExtractor = Keys.getKeyByName(IHeadersExtractor.class.getCanonicalName());
        IKey keyResponseStatusExtractor = Keys.getKeyByName(IResponseStatusExtractor.class.getCanonicalName());
        IKey keyFullHttpResponse = Keys.getKeyByName(DefaultFullHttpResponse.class.getCanonicalName());

        IOC.register(
                keyCookiesExtractor,
                new SingletonStrategy(
                        cookiesExtractor
                )
        );
        IOC.register(
                keyHeadersExtractor,
                new SingletonStrategy(
                        headersExtractor
                )
        );
        IOC.register(
                keyResponseStatusExtractor,
                new SingletonStrategy(
                        responseStatusExtractor
                )
        );


        IOC.register(
                keyIObject,
                new ApplyFunctionToArgumentsStrategy(
                        (args) -> {
                            try {
                                return new DSObject((String) args[0]);
                            } catch (InvalidArgumentException ignored) {
                            }
                            return null;
                        }
                )
        );
        IOC.register(
                keyFullHttpResponse,
                new ApplyFunctionToArgumentsStrategy(
                        (args) ->
                                new DefaultFullHttpResponse((HttpVersion) args[0], (HttpResponseStatus) args[1], (ByteBuf) args[2])

                )
        );
        IOC.register(
                keyFieldName,
                new ApplyFunctionToArgumentsStrategy(
                        (args) -> {
                            try {
                                return new FieldName((String) args[0]);
                            } catch (InvalidArgumentException ignored) {
                            }
                            return null;
                        }
                )
        );

        IOC.register(Keys.getKeyByName("key_for_response_status_setter"), new SingletonStrategy(key));
        IOC.register(Keys.getKeyByName("key_for_headers_extractor"), new SingletonStrategy(key));
        IOC.register(Keys.getKeyByName("key_for_cookies_extractor"), new SingletonStrategy(key));

    }

    @Test
    public void writeShouldCallSendMethod() throws
            ResponseSendingException, ResolutionException, CookieSettingException, HeadersSetterException {
        HttpResponseSender sender = new HttpResponseSender("123");
        IObject environment = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"), "{\"foo\":\"bar\"}");
        when(responseStatusExtractor.extract(any(IObject.class))).thenReturn(200);
        when(response.getContent()).thenReturn("{\"foo\":\"bar\"}".getBytes());
        sender.send(response, environment, ctx);
        verify(cookiesExtractor, times(1)).set(any(FullHttpResponse.class), any(IObject.class));
        verify(headersExtractor, times(1)).set(any(FullHttpResponse.class), any(IObject.class));
        verify(ctx, times(1)).send(any(FullHttpResponse.class));
    }
}
