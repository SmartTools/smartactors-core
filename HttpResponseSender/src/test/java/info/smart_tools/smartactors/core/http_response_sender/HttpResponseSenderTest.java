package info.smart_tools.smartactors.core.http_response_sender;

import info.smart_tools.smartactors.core.create_new_instance_strategy.CreateNewInstanceStrategy;
import info.smart_tools.smartactors.core.ds_object.DSObject;
import info.smart_tools.smartactors.core.field_name.FieldName;
import info.smart_tools.smartactors.core.ichannel_handler.IChannelHandler;
import info.smart_tools.smartactors.core.icookies_extractor.ICookiesSetter;
import info.smart_tools.smartactors.core.icookies_extractor.exceptions.CookieSettingException;
import info.smart_tools.smartactors.core.iheaders_extractor.IHeadersSetter;
import info.smart_tools.smartactors.core.iheaders_extractor.exceptions.HeadersSetterException;
import info.smart_tools.smartactors.core.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.iresponse.IResponse;
import info.smart_tools.smartactors.core.iresponse_sender.exceptions.ResponseSendingException;
import info.smart_tools.smartactors.core.iresponse_status_extractor.IResponseStatusExtractor;
import info.smart_tools.smartactors.core.iscope.IScope;
import info.smart_tools.smartactors.core.iscope_provider_container.exception.ScopeProviderException;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.core.resolve_by_name_ioc_strategy.ResolveByNameIocStrategy;
import info.smart_tools.smartactors.core.scope_provider.ScopeProvider;
import info.smart_tools.smartactors.core.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.core.strategy_container.StrategyContainer;
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
    private IHeadersSetter headersExtractor;
    private IResponseStatusExtractor responseStatusExtractor;
    private IResponse response;

    @Before
    public void setUp() throws ScopeProviderException, RegistrationException, ResolutionException, InvalidArgumentException {
        ctx = mock(IChannelHandler.class);
        cookiesExtractor = mock(ICookiesSetter.class);
        headersExtractor = mock(IHeadersSetter.class);
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
                IOC.getKeyForKeyStorage(),
                new ResolveByNameIocStrategy()
        );
        IKey keyIObject = Keys.getOrAdd(IObject.class.getCanonicalName());
        IKey keyFieldName = Keys.getOrAdd(FieldName.class.getCanonicalName());
        IKey keyCookiesExtractor = Keys.getOrAdd(ICookiesSetter.class.getCanonicalName());
        IKey keyHeadersExtractor = Keys.getOrAdd(IHeadersSetter.class.getCanonicalName());
        IKey keyResponseStatusExtractor = Keys.getOrAdd(IResponseStatusExtractor.class.getCanonicalName());
        IKey keyFullHttpResponse = Keys.getOrAdd(DefaultFullHttpResponse.class.getCanonicalName());

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
                new CreateNewInstanceStrategy(
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
                new CreateNewInstanceStrategy(
                        (args) ->
                                new DefaultFullHttpResponse((HttpVersion) args[0], (HttpResponseStatus) args[1], (ByteBuf) args[2])

                )
        );
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

    }

    @Test
    public void writeShouldCallSendMethod() throws
            ResponseSendingException, ResolutionException, CookieSettingException, HeadersSetterException {
        HttpResponseSender sender = new HttpResponseSender("123");
        IObject environment = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()), "{\"foo\":\"bar\"}");
        when(responseStatusExtractor.extract(any(IObject.class))).thenReturn(200);
        when(response.getContent()).thenReturn("{\"foo\":\"bar\"}".getBytes());
        sender.send(response, environment, ctx);
        verify(cookiesExtractor, times(1)).set(any(FullHttpResponse.class), any(IObject.class));
        verify(headersExtractor, times(1)).set(any(FullHttpResponse.class), any(IObject.class));
        verify(ctx, times(1)).send(any(FullHttpResponse.class));
    }
}
