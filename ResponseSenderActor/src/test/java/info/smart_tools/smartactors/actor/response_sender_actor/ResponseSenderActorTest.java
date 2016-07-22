package info.smart_tools.smartactors.actor.response_sender_actor;

import info.smart_tools.smartactors.actor.response_sender_actor.wrapper.ResponseMessage;
import info.smart_tools.smartactors.core.create_new_instance_strategy.CreateNewInstanceStrategy;
import info.smart_tools.smartactors.core.ds_object.DSObject;
import info.smart_tools.smartactors.core.field_name.FieldName;
import info.smart_tools.smartactors.core.ichannel_handler.IChannelHandler;
import info.smart_tools.smartactors.core.ifield_name.IFieldName;
import info.smart_tools.smartactors.core.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.iobject.exception.SerializeException;
import info.smart_tools.smartactors.core.iobject_wrapper.IObjectWrapper;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.iresponse.IResponse;
import info.smart_tools.smartactors.core.iresponse_content_strategy.IResponseContentStrategy;
import info.smart_tools.smartactors.core.iresponse_sender.IResponseSender;
import info.smart_tools.smartactors.core.iresponse_sender.exceptions.ResponseSendingException;
import info.smart_tools.smartactors.core.iscope.IScope;
import info.smart_tools.smartactors.core.iscope_provider_container.exception.ScopeProviderException;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.core.resolve_by_name_ioc_strategy.ResolveByNameIocStrategy;
import info.smart_tools.smartactors.core.scope_provider.ScopeProvider;
import info.smart_tools.smartactors.core.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.core.strategy_container.StrategyContainer;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class ResponseSenderActorTest {
    IResponse response;
    IResponseContentStrategy responseContentStrategy;
    IResponseSender responseSender;

    @Before
    public void setUp() throws ScopeProviderException, ResolutionException, RegistrationException, InvalidArgumentException {
        response = mock(IResponse.class);
        responseContentStrategy = mock(IResponseContentStrategy.class);
        responseSender = mock(IResponseSender.class);
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
        IOC.register(keyFieldName,
                new CreateNewInstanceStrategy(
                        (args) -> {
                            try {
                                return new FieldName((String) args[0]);
                            } catch (InvalidArgumentException ignored) {
                            }
                            return null;
                        }
                ));
        IKey keyIObject = Keys.getOrAdd(IObject.class.getCanonicalName());
        IOC.register(keyIObject,
                new CreateNewInstanceStrategy(
                        (args) -> new DSObject()

                ));
        IKey keyIResponse = Keys.getOrAdd(IResponse.class.getCanonicalName());
        IOC.register(keyIResponse,
                new SingletonStrategy(response));

        IKey keyIResponseContentStrategy = Keys.getOrAdd(IResponseContentStrategy.class.getCanonicalName());
        IOC.register(keyIResponseContentStrategy,
                new SingletonStrategy(responseContentStrategy));

        IKey keyIResponseSender = Keys.getOrAdd(IResponseSender.class.getCanonicalName());
        IOC.register(keyIResponseSender,
                new SingletonStrategy(responseSender));


    }

    @Test
    public void testResponseSenderActorShouldHandleSend()
            throws InvalidArgumentException, SerializeException, ReadValueException,
            ChangeValueException, ResponseSendingException, ResolutionException {
        IObject environment =
                new DSObject("{\"config\": null, \"message\": null, \"response\": {\"hello\":\"world\"}}");
        IObject context = new DSObject();
        context.setValue(new FieldName("channel"), null);
        environment.setValue(new FieldName("context"), context);
        Wrapper wrapper = new Wrapper();
        wrapper.init(environment);
        ResponseSenderActor senderActor = new ResponseSenderActor();
        senderActor.sendResponse(wrapper);
        verify(responseSender, times(1)).send(any(IResponse.class), any(IObject.class), any(IChannelHandler.class));
    }
}

class Wrapper implements IObjectWrapper, ResponseMessage {
    IObject environment;

    @Override
    public IChannelHandler getChannelHandler() {
        return null;
    }

    @Override
    public IObject getResponse() {
        return null;
    }

    @Override
    public void init(IObject environment) {
        this.environment = environment;
    }

    @Override
    public IObject getEnvironmentIObject(IFieldName fieldName) throws InvalidArgumentException {
        try {
            return (IObject) environment.getValue(fieldName);
        } catch (ReadValueException e) {
            throw new InvalidArgumentException("Invalid fieldName", e);
        }
    }
}
