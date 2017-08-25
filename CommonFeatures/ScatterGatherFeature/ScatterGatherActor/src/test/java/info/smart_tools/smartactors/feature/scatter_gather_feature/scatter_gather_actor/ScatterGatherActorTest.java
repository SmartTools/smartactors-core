package info.smart_tools.smartactors.feature.scatter_gather_feature.scatter_gather_actor;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.feature.scatter_gather_feature.iscatter_gather_strategy.IScatterGatherStrategy;
import info.smart_tools.smartactors.feature.scatter_gather_feature.iscatter_gather_strategy.exception.IScatterGatherStrategyException;
import info.smart_tools.smartactors.feature.scatter_gather_feature.scatter_gather_actor.exception.ScatterGatherActorException;
import info.smart_tools.smartactors.feature.scatter_gather_feature.scatter_gather_actor.wrapper.GatherWrapper;
import info.smart_tools.smartactors.feature.scatter_gather_feature.scatter_gather_actor.wrapper.ScatterWrapper;
import info.smart_tools.smartactors.field_plugins.ifield_plugin.IFieldPlugin;
import info.smart_tools.smartactors.helpers.plugins_loading_test_base.PluginsLoadingTestBase;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.iobject_plugins.dsobject_plugin.PluginDSObject;
import info.smart_tools.smartactors.iobject_plugins.ifieldname_plugin.IFieldNamePlugin;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import info.smart_tools.smartactors.ioc_plugins.ioc_keys_plugin.PluginIOCKeys;
import info.smart_tools.smartactors.message_bus.interfaces.imessage_bus_handler.IMessageBusHandler;
import info.smart_tools.smartactors.message_bus.interfaces.imessage_bus_handler.exception.MessageBusHandlerException;
import info.smart_tools.smartactors.message_bus.message_bus.MessageBus;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.scope.scope_provider.ScopeProvider;
import info.smart_tools.smartactors.scope_plugins.scope_provider_plugin.PluginScopeProvider;
import info.smart_tools.smartactors.scope_plugins.scoped_ioc_plugin.ScopedIOCPlugin;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by sevenbits on 08.02.17.
 */
public class ScatterGatherActorTest extends PluginsLoadingTestBase {
    private IMessageBusHandler messageBusHandler = mock(IMessageBusHandler.class);
    private ScatterWrapper scatterWrapper = mock(ScatterWrapper.class);
    private GatherWrapper gatherWrapper = mock(GatherWrapper.class);
    private Object chainName = mock(Object.class);
    private IObject initIObject;

    @Before
    public void setUp() throws Exception {
        ScopeProvider.getCurrentScope().setValue(MessageBus.getMessageBusKey(), messageBusHandler);
        loadPlugins();
        initIObject = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()), "{\"strategyDependency\": \"name\"}");
    }

    @Override
    protected void loadPlugins() throws Exception {
        load(ScopedIOCPlugin.class);
        load(PluginScopeProvider.class);
        load(PluginIOCKeys.class);
        load(IFieldNamePlugin.class);
        load(IFieldPlugin.class);
        load(PluginDSObject.class);
    }

    @Test
    public void testScatterWithActualObjects() throws ReadValueException, IScatterGatherStrategyException, ResolutionException, InvalidArgumentException, RegistrationException, ScatterGatherActorException, MessageBusHandlerException {
        IMessageProcessor messageProcessor = mock(IMessageProcessor.class);
        IScatterGatherStrategy strategy = mock(IScatterGatherStrategy.class);
        when(strategy.chainChoose(any())).thenReturn(chainName);
        when(strategy.chooseReplyChain(any())).thenReturn(chainName);
        when(scatterWrapper.getMessageProcessor()).thenReturn(messageProcessor);
        when(strategy.formIObjectToSentFromObject(any())).thenAnswer(
                new Answer<IObject>() {
                    @Override
                    public IObject answer(InvocationOnMock invocationOnMock) throws Throwable {
                        Object object = invocationOnMock.getArguments()[0];
                        return (IObject) object;
                    }
                }
        );
        IObject scatterObject1 = mock(IObject.class);
        IObject scatterObject2 = mock(IObject.class);
        IObject scatterObject3 = mock(IObject.class);
        List<Object> scatterCollection = Arrays.asList(scatterObject1, scatterObject2, scatterObject3);
        when(scatterWrapper.getCollection()).thenReturn(scatterCollection);
        IOC.register(Keys.getOrAdd("name"), new SingletonStrategy(strategy));
        ScatterGatherActor actor = new ScatterGatherActor(initIObject);
        actor.scatter(scatterWrapper);
        verify(messageBusHandler).handleForReply(scatterObject1, chainName, chainName);
        verify(messageBusHandler).handleForReply(scatterObject2, chainName, chainName);
        verify(messageBusHandler).handleForReply(scatterObject3, chainName, chainName);
    }

    @Test
    public void testOnMessageSendFailedCallsOnMessageSendException() throws ReadValueException, IScatterGatherStrategyException,
            ResolutionException, InvalidArgumentException, RegistrationException,
            ScatterGatherActorException, MessageBusHandlerException, ChangeValueException {
        IMessageProcessor messageProcessor = mock(IMessageProcessor.class);
        IScatterGatherStrategy strategy = mock(IScatterGatherStrategy.class);
        when(strategy.chainChoose(any())).thenReturn(chainName);
        when(scatterWrapper.getMessageProcessor()).thenReturn(messageProcessor);
        doThrow(new MessageBusHandlerException("test message")).when(messageBusHandler).handleForReply(any(), any(), any());
        IObject scatterObject1 = mock(IObject.class);
        IObject scatterObject2 = mock(IObject.class);
        IObject scatterObject3 = mock(IObject.class);
        List<Object> scatterCollection = Arrays.asList(scatterObject1, scatterObject2, scatterObject3);
        when(scatterWrapper.getCollection()).thenReturn(scatterCollection);
        IOC.register(Keys.getOrAdd("name"), new SingletonStrategy(strategy));
        ScatterGatherActor actor = new ScatterGatherActor(initIObject);
        actor.scatter(scatterWrapper);
        verify(strategy, times(scatterCollection.size())).onMessageSendFail(any(IMessageProcessor.class), any(IObject.class));
    }

    @Test
    public void testStrategyMethodOnMessageSendCallsOnMessageSent() throws ReadValueException, IScatterGatherStrategyException,
            ResolutionException, InvalidArgumentException, RegistrationException,
            ScatterGatherActorException, MessageBusHandlerException, ChangeValueException {
        IMessageProcessor messageProcessor = mock(IMessageProcessor.class);
        IScatterGatherStrategy strategy = mock(IScatterGatherStrategy.class);
        when(strategy.chainChoose(any())).thenReturn(chainName);
        when(scatterWrapper.getMessageProcessor()).thenReturn(messageProcessor);
        IObject scatterObject1 = mock(IObject.class);
        IObject scatterObject2 = mock(IObject.class);
        IObject scatterObject3 = mock(IObject.class);
        List<Object> scatterCollection = Arrays.asList(scatterObject1, scatterObject2, scatterObject3);
        when(scatterWrapper.getCollection()).thenReturn(scatterCollection);
        IOC.register(Keys.getOrAdd("name"), new SingletonStrategy(strategy));
        ScatterGatherActor actor = new ScatterGatherActor(initIObject);
        actor.scatter(scatterWrapper);
        verify(strategy, times(scatterCollection.size())).onMessageSent(eq(messageProcessor), any(IObject.class));
    }

    @Test
    public void testStrategyMethodBeforeStartScatterCalls() throws ReadValueException, IScatterGatherStrategyException,
            ResolutionException, InvalidArgumentException, RegistrationException,
            ScatterGatherActorException, MessageBusHandlerException, ChangeValueException {
        IMessageProcessor messageProcessor = mock(IMessageProcessor.class);
        IScatterGatherStrategy strategy = mock(IScatterGatherStrategy.class);
        when(strategy.chainChoose(any())).thenReturn(chainName);
        when(scatterWrapper.getMessageProcessor()).thenReturn(messageProcessor);
        IObject scatterObject1 = mock(IObject.class);
        IObject scatterObject2 = mock(IObject.class);
        IObject scatterObject3 = mock(IObject.class);
        List<Object> scatterCollection = Arrays.asList(scatterObject1, scatterObject2, scatterObject3);
        when(scatterWrapper.getCollection()).thenReturn(scatterCollection);
        IOC.register(Keys.getOrAdd("name"), new SingletonStrategy(strategy));
        ScatterGatherActor actor = new ScatterGatherActor(initIObject);
        actor.scatter(scatterWrapper);
        verify(strategy, times(1)).beforeScatter(eq(messageProcessor), any(IObject.class));
    }

    @Test
    public void testStrategyMethodAfterScatterCalls() throws ReadValueException, IScatterGatherStrategyException,
            ResolutionException, InvalidArgumentException, RegistrationException,
            ScatterGatherActorException, MessageBusHandlerException, ChangeValueException {
        IMessageProcessor messageProcessor = mock(IMessageProcessor.class);
        IScatterGatherStrategy strategy = mock(IScatterGatherStrategy.class);
        when(strategy.chainChoose(any())).thenReturn(chainName);
        when(scatterWrapper.getMessageProcessor()).thenReturn(messageProcessor);
        IObject scatterObject1 = mock(IObject.class);
        IObject scatterObject2 = mock(IObject.class);
        IObject scatterObject3 = mock(IObject.class);
        List<Object> scatterCollection = Arrays.asList(scatterObject1, scatterObject2, scatterObject3);
        when(scatterWrapper.getCollection()).thenReturn(scatterCollection);
        IOC.register(Keys.getOrAdd("name"), new SingletonStrategy(strategy));
        ScatterGatherActor actor = new ScatterGatherActor(initIObject);
        actor.scatter(scatterWrapper);
        verify(strategy, times(1)).afterScatter(eq(messageProcessor), any(IObject.class));
    }


    @Test
    public void testStrategyMethodOnGatherCalls() throws ReadValueException, IScatterGatherStrategyException,
            ResolutionException, InvalidArgumentException, RegistrationException,
            ScatterGatherActorException, MessageBusHandlerException, ChangeValueException {
        IMessageProcessor messageProcessor = mock(IMessageProcessor.class);
        IScatterGatherStrategy strategy = mock(IScatterGatherStrategy.class);

        //scatter section to save message processor at actor
        when(strategy.chainChoose(any())).thenReturn(chainName);
        when(scatterWrapper.getMessageProcessor()).thenReturn(messageProcessor);
        when(scatterWrapper.getCollection()).thenReturn(new ArrayList<Object>());

        when(strategy.chainChoose(any())).thenReturn(chainName);
        IObject resultIObject = mock(IObject.class);
        when(gatherWrapper.getResult()).thenReturn(resultIObject);
        IOC.register(Keys.getOrAdd("name"), new SingletonStrategy(strategy));
        ScatterGatherActor actor = new ScatterGatherActor(initIObject);
        actor.scatter(scatterWrapper);
        actor.gather(gatherWrapper);
        verify(strategy, times(1)).onGather(eq(messageProcessor), eq(resultIObject), any(IObject.class));
    }

}
