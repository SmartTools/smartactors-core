package info.smart_tools.smartactors.http_endpoint.deserilize_strategy_post_form_urlencoded;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.http_endpoint.deserialize_strategy_post_form_urlencoded.DeserializeStrategyPostFormUrlencoded;
import info.smart_tools.smartactors.iobject.ds_object.DSObject;
import info.smart_tools.smartactors.iobject.field_name.FieldName;
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
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.FullHttpRequest;
import org.junit.Before;
import org.junit.Test;

import static com.google.common.base.Verify.verify;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DeserializeStrategyPostFormUrlencodedTest {
    @Before
    public void setUp() throws ScopeProviderException, ResolutionException, RegistrationException, InvalidArgumentException {
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
        IKey keyIObject = Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject");
        IOC.register(
                keyIObject,
                new ApplyFunctionToArgumentsStrategy(
                    (args) -> new DSObject()

                )
        );
        IOC.register(
                Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"),
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
    }

    @Test
    public void testDeserializationResult() throws Exception {
        ByteBuf bytebuf = Unpooled.wrappedBuffer("hello=world".getBytes());
        FullHttpRequest request = mock(FullHttpRequest.class);
        when(request.content()).thenReturn(bytebuf);
        DeserializeStrategyPostFormUrlencoded deserializeStrategy = new DeserializeStrategyPostFormUrlencoded();
        IObject iObject = deserializeStrategy.deserialize(request);
        String iObjectString = iObject.serialize().toString();
        verify(iObjectString.equals("{\"hello\":\"world\"}"));
    }
}
