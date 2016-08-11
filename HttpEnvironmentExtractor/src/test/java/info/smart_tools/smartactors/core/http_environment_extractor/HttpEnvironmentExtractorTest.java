package info.smart_tools.smartactors.core.http_environment_extractor;

import info.smart_tools.smartactors.core.channel_handler_netty.ChannelHandlerNetty;
import info.smart_tools.smartactors.core.create_new_instance_strategy.CreateNewInstanceStrategy;
import info.smart_tools.smartactors.core.ds_object.DSObject;
import info.smart_tools.smartactors.core.field_name.FieldName;
import info.smart_tools.smartactors.core.ichannel_handler.IChannelHandler;
import info.smart_tools.smartactors.core.ideserialize_strategy.IDeserializeStrategy;
import info.smart_tools.smartactors.core.ideserialize_strategy.exceptions.DeserializationException;
import info.smart_tools.smartactors.core.ienvironment_extractor.exceptions.EnvironmentExtractionException;
import info.smart_tools.smartactors.core.ifield_name.IFieldName;
import info.smart_tools.smartactors.core.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
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
import io.netty.handler.codec.http.FullHttpRequest;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpEnvironmentExtractorTest {
    private IDeserializeStrategy deserializeStrategy;
    private FullHttpRequest httpRequest;
    private IChannelHandler channelHandler;

    @Before
    public void setUp() throws ScopeProviderException, RegistrationException, ResolutionException, InvalidArgumentException {
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
        IOC.register(Keys.getOrAdd(IFieldName.class.getCanonicalName()),
                new CreateNewInstanceStrategy(
                        args -> {
                            try {
                                return new FieldName((String) args[0]);
                            } catch (InvalidArgumentException e) {
                            }
                            return null;
                        }
                )
        );
        IOC.register(Keys.getOrAdd("EmptyIObject"),
                new CreateNewInstanceStrategy(
                        args -> new DSObject()

                )
        );

        IOC.register(Keys.getOrAdd(ChannelHandlerNetty.class.getCanonicalName()),
                new CreateNewInstanceStrategy(
                        args -> args[0]
                )
        );

        deserializeStrategy = mock(IDeserializeStrategy.class);
        httpRequest = mock(FullHttpRequest.class);
        channelHandler = mock(IChannelHandler.class);

        IOC.register(Keys.getOrAdd(IDeserializeStrategy.class.getCanonicalName()),
                new SingletonStrategy(deserializeStrategy)
        );
    }

    @Test
    public void environmentExtractorShouldExtractFromHttpRequest()
            throws EnvironmentExtractionException, InvalidArgumentException, ReadValueException, DeserializationException {
        HttpEnvironmentExtractor environmentExtractor = new HttpEnvironmentExtractor();
        IObject message = new DSObject("{\"hello\": \"world\"}");

        when(deserializeStrategy.deserialize(httpRequest)).thenReturn(message);

        IObject environment = environmentExtractor.extract(httpRequest, channelHandler);
        assertEquals(environment.getValue(new FieldName("message")), message);
        assertEquals(((IObject) environment.getValue(new FieldName("context"))).getValue(new FieldName("channel")), channelHandler);
        assertEquals(((IObject) environment.getValue(new FieldName("context"))).getValue(new FieldName("request")), httpRequest);
    }
}
