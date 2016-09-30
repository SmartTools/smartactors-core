package info.smart_tools.smartactors.core.http_response_handler;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.strategy.create_new_instance_strategy.CreateNewInstanceStrategy;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.core.IDeserializeStrategy;
import info.smart_tools.smartactors.core.exceptions.DeserializationException;
import info.smart_tools.smartactors.core.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.iqueue.IQueue;
import info.smart_tools.smartactors.core.itask.ITask;
import info.smart_tools.smartactors.core.message_processing.IMessageProcessingSequence;
import info.smart_tools.smartactors.core.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.core.message_processing.IReceiverChain;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.core.resolve_by_name_ioc_strategy.ResolveByNameIocStrategy;
import info.smart_tools.smartactors.core.strategy_container.StrategyContainer;
import info.smart_tools.smartactors.iobject.ds_object.DSObject;
import info.smart_tools.smartactors.iobject.field_name.FieldName;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.scope.iscope.IScope;
import info.smart_tools.smartactors.scope.iscope_provider_container.exception.ScopeProviderException;
import info.smart_tools.smartactors.scope.scope_provider.ScopeProvider;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by sevenbits on 30.09.16.
 */
public class HttpResponseHandlerTest {
    IQueue<ITask> taskQueue;
    IReceiverChain receiverChain;
    HttpResponseHandler responseHandler;
    IMessageProcessingSequence messageProcessingSequence;
    IMessageProcessor messageProcessor;
    FullHttpResponse response;
    IDeserializeStrategy strategy;
    HttpHeaders headers;
    ChannelHandlerContext ctx;

    @Before
    public void setUp() throws ScopeProviderException, RegistrationException, ResolutionException, InvalidArgumentException {
        this.taskQueue = mock(IQueue.class);
        this.receiverChain = mock(IReceiverChain.class);
        this.messageProcessingSequence = mock(IMessageProcessingSequence.class);
        this.messageProcessor = mock(IMessageProcessor.class);
        this.response = mock(FullHttpResponse.class);
        this.strategy = mock(IDeserializeStrategy.class);
        this.ctx = mock(ChannelHandlerContext.class);
        this.headers = mock(HttpHeaders.class);
        this.responseHandler = new HttpResponseHandler(taskQueue, 5, receiverChain);
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
                        (args) -> {
                            try {
                                return new FieldName((String) args[0]);
                            } catch (InvalidArgumentException e) {
                                throw new RuntimeException(e);
                            }
                        }
                )
        );
        IOC.register(Keys.getOrAdd(IMessageProcessingSequence.class.getCanonicalName()),
                new SingletonStrategy(
                        messageProcessingSequence
                )
        );
        IOC.register(Keys.getOrAdd(IMessageProcessor.class.getCanonicalName()),
                new SingletonStrategy(
                        messageProcessor
                )
        );
        IOC.register(Keys.getOrAdd("httpResponseResolver"), new SingletonStrategy(
                        strategy
                )
        );
        IOC.register(Keys.getOrAdd("EmptyIObject"), new CreateNewInstanceStrategy(
                        (args) -> new DSObject()

                )
        );
    }

    @Test
    public void testNewTaskAddedToQueue() throws InvalidArgumentException, DeserializationException, InterruptedException {
        String chainName = "chainName";
        when(response.headers()).thenReturn(headers);
        when(headers.get("messageMapId")).thenReturn("messageMap");
        when(strategy.deserialize(any())).thenReturn(new DSObject("{\"foo\": \"bar\"}"));
        responseHandler.handle(ctx, response);
        verify(taskQueue, times(1)).put(any());
    }
}
