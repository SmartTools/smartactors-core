package info.smart_tools.smartactors.endpoint.actor.response_sender_actor;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.strategy.create_new_instance_strategy.CreateNewInstanceStrategy;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.endpoint.actor.response_sender_actor.exceptions.ResponseSenderActorException;
import info.smart_tools.smartactors.endpoint.actor.response_sender_actor.wrapper.ResponseSenderMessage;
import info.smart_tools.smartactors.endpoint.interfaces.ichannel_handler.IChannelHandler;
import info.smart_tools.smartactors.endpoint.interfaces.iresponse.IResponse;
import info.smart_tools.smartactors.endpoint.interfaces.iresponse_content_strategy.IResponseContentStrategy;
import info.smart_tools.smartactors.endpoint.interfaces.iresponse_sender.IResponseSender;
import info.smart_tools.smartactors.endpoint.interfaces.iresponse_sender.exceptions.ResponseSendingException;
import info.smart_tools.smartactors.iobject.ds_object.DSObject;
import info.smart_tools.smartactors.iobject.field_name.FieldName;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.SerializeException;
import info.smart_tools.smartactors.iobject.iobject_wrapper.IObjectWrapper;
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
                IOC.getKeyForKeyByNameStrategy(),
                new ResolveByNameIocStrategy()
        );
        IKey keyFieldName = Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName");
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
        IKey keyIObject = Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject");
        IOC.register(keyIObject,
                new CreateNewInstanceStrategy(
                        (args) -> new DSObject()

                ));
        IKey keyIResponse = Keys.getKeyByName(IResponse.class.getCanonicalName());
        IOC.register(keyIResponse,
                new SingletonStrategy(response));

        IKey keyIResponseContentStrategy = Keys.getKeyByName(IResponseContentStrategy.class.getCanonicalName());
        IOC.register(keyIResponseContentStrategy,
                new SingletonStrategy(responseContentStrategy));

        IKey keyIResponseSender = Keys.getKeyByName(IResponseSender.class.getCanonicalName());
        IOC.register(keyIResponseSender,
                new SingletonStrategy(responseSender));

        IOC.register(Keys.getKeyByName("http_request_key_for_response_sender"),
                new SingletonStrategy("HTTP_POST"));


    }

    @Test
    public void testResponseSenderActorShouldHandleSend()
            throws InvalidArgumentException, SerializeException, ReadValueException, ResolutionException,
            InvalidArgumentException, ResponseSenderActorException, ChangeValueException, ResponseSendingException {
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

class Wrapper implements IObjectWrapper, ResponseSenderMessage {
    IObject environment;

//    @Override
//    public IChannelHandler getChannelHandler() {
//        return null;
//    }
//
//    @Override
//    public IObject getResponse() {
//        return null;
//    }

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
