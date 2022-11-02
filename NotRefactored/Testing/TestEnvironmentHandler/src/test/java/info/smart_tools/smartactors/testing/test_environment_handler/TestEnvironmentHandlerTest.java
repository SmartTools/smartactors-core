package info.smart_tools.smartactors.testing.test_environment_handler;

import info.smart_tools.smartactors.base.exception.initialization_exception.InitializationException;
import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.endpoint.interfaces.ienvironment_handler.exception.EnvironmentHandleException;
import info.smart_tools.smartactors.helpers.plugins_loading_test_base.PluginsLoadingTestBase;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject_plugins.dsobject_plugin.PluginDSObject;
import info.smart_tools.smartactors.iobject_plugins.ifieldname_plugin.IFieldNamePlugin;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.ioc_plugins.ioc_keys_plugin.PluginIOCKeys;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessingSequence;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IReceiverChain;
import info.smart_tools.smartactors.scope_plugins.scope_provider_plugin.PluginScopeProvider;
import info.smart_tools.smartactors.scope_plugins.scoped_ioc_plugin.ScopedIOCPlugin;
import info.smart_tools.smartactors.task.interfaces.iqueue.IQueue;
import info.smart_tools.smartactors.task.interfaces.itask.ITask;
import info.smart_tools.smartactors.testing.interfaces.iresult_checker.IResultChecker;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

/**
 * Test for {@link TestEnvironmentHandler}.
 */
public class TestEnvironmentHandlerTest extends PluginsLoadingTestBase {
    private IObject messageMock;
    private IObject contextMock;
    private IMessageProcessor messageProcessorMock;
    private IQueue<ITask> taskQueueMock;
    private IReceiverChain testedChainMock;
    private Object chainNameMock;
    private MainTestChain mainTestChainMock;
    private IMessageProcessingSequence sequenceMock;
    private IAction<Throwable> callbackMock;

    private IStrategy mainTestChainStrategyMock;
    private IStrategy sequenceStrategyMock;
    private IStrategy mpStrategyMock;
    private IStrategy createAssertCheckerStrategyMock;
    private IStrategy createInterceptCheckerStrategyMock;
    private IResultChecker assertChecker;
    private IResultChecker interceptChecker;

//    private ArgumentCaptor<IAction> cbCaptor = ArgumentCaptor.forClass(IAction.class);

    @Override
    protected void loadPlugins()
            throws Exception {
        load(ScopedIOCPlugin.class);
        load(PluginScopeProvider.class);
        load(PluginIOCKeys.class);
        load(PluginDSObject.class);
        load(IFieldNamePlugin.class);
    }

    @Override
    protected void registerMocks()
            throws Exception {
        messageMock = mock(IObject.class);
        contextMock = mock(IObject.class);
        messageProcessorMock = mock(IMessageProcessor.class);
        taskQueueMock = mock(IQueue.class);
        testedChainMock = mock(IReceiverChain.class);
        mainTestChainMock = mock(MainTestChain.class);
        sequenceMock = mock(IMessageProcessingSequence.class);
        callbackMock = mock(IAction.class);
        createAssertCheckerStrategyMock = mock(IStrategy.class);
        createInterceptCheckerStrategyMock = mock(IStrategy.class);
        assertChecker = mock(IResultChecker.class);
        interceptChecker = mock(IResultChecker.class);
        chainNameMock = mock(Object.class);

        when(messageProcessorMock.getSequence()).thenReturn(sequenceMock);

        mainTestChainStrategyMock = mock(IStrategy.class);
        sequenceStrategyMock = mock(IStrategy.class);
        mpStrategyMock = mock(IStrategy.class);

        when(mpStrategyMock.resolve(same(taskQueueMock), same(sequenceMock))).thenReturn(messageProcessorMock);
        when(sequenceStrategyMock.resolve(any(), same(mainTestChainMock))).thenReturn(sequenceMock);
        when(mainTestChainStrategyMock.resolve(any(Object.class), any(IAction.class), any(IObject.class),
                any(), any())).thenReturn(mainTestChainMock);
        when(createAssertCheckerStrategyMock.resolve(any(ArrayList.class))).thenReturn(assertChecker);
        when(createInterceptCheckerStrategyMock.resolve(any(IObject.class))).thenReturn(interceptChecker);

        IOC.register(Keys.getKeyByName("task_queue"), new SingletonStrategy(taskQueueMock));
        IOC.register(Keys.getKeyByName(MainTestChain.class.getCanonicalName()), mainTestChainStrategyMock);
        IOC.register(Keys.getKeyByName("info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessingSequence"), sequenceStrategyMock);
        IOC.register(Keys.getKeyByName("info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor"), mpStrategyMock);
        IOC.register(Keys.getKeyByName(IResultChecker.class.getCanonicalName() + "#assert"), createAssertCheckerStrategyMock);
        IOC.register(Keys.getKeyByName(IResultChecker.class.getCanonicalName() + "#intercept"), createInterceptCheckerStrategyMock);
    }

    @Test(expected = InvalidArgumentException.class)
    public void Should_throwWhenDescriptionIsNull()
            throws Exception {
        new TestEnvironmentHandler().handle(null, mock(IReceiverChain.class), mock(IAction.class));
    }

    @Test(expected = InvalidArgumentException.class)
    public void Should_throwWhenCallbackIsNull()
            throws Exception {
        new TestEnvironmentHandler().handle(mock(IObject.class), mock(IReceiverChain.class), null);
    }

    @Test(expected = InvalidArgumentException.class)
    public void Should_throwWhenChainIsNull()
            throws Exception {
        new TestEnvironmentHandler().handle(mock(IObject.class), null, mock(IAction.class));
    }

    @Test
    public void Should_runTestWithAssert()
            throws Exception {
        IObject env = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"));
        env.setValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "message"), messageMock);
        env.setValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "context"), contextMock);

        IObject desc = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                "{'name': 'The test', 'chainName': 'chainToTest', 'assert': []}".replace('\'','"'));

        desc.setValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "environment"), env);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                callbackMock.execute(null);
                return null;
            }
        }).when(this.messageProcessorMock).process(any(), any());

        new TestEnvironmentHandler().handle(desc, chainNameMock, callbackMock);

        verify(callbackMock).execute(null);
    }

    @Test
    public void Should_runTestWithIntercept()
            throws Exception {
        IObject env = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"));
        env.setValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "message"), messageMock);
        env.setValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "context"), contextMock);

        IObject desc = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                "{'name': 'The test', 'chainName': 'chainToTest', 'intercept': {}}".replace('\'','"'));

        desc.setValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "environment"), env);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                callbackMock.execute(null);
                return null;
            }
        }).when(this.messageProcessorMock).process(any(), any());

        new TestEnvironmentHandler().handle(desc, chainNameMock, callbackMock);

        verify(callbackMock).execute(null);
    }


    @Test
    public void Should_runTestWhenItFails()
            throws Exception {
        IObject env = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"));
        env.setValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "message"), messageMock);
        env.setValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "context"), contextMock);

        IObject desc = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                "{'name': 'The test', 'chainName': 'chainToTest', 'assert': []}".replace('\'','"'));

        desc.setValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "environment"), env);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                callbackMock.execute(new Exception());
                return null;
            }
        }).when(this.messageProcessorMock).process(any(), any());

        new TestEnvironmentHandler().handle(desc, chainNameMock, callbackMock);

        verify(callbackMock).execute(any());
    }

    @Test (expected = EnvironmentHandleException.class)
    public void Should_throwWhenDescriptionDoesNotContainsAssertAndInterceptSection()
            throws Exception {
        IObject env = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"));
        env.setValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "message"), messageMock);
        env.setValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "context"), contextMock);

        IObject desc = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                "{'name': 'The test', 'chainName': 'chainToTest'}".replace('\'','"'));

        desc.setValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "environment"), env);

        new TestEnvironmentHandler().handle(desc, chainNameMock, callbackMock);
        fail();
    }

    @Test (expected = EnvironmentHandleException.class)
    public void Should_throwWhenDescriptionContainsBothAssertAndInterceptSection()
            throws Exception {
        IObject env = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"));
        env.setValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "message"), messageMock);
        env.setValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "context"), contextMock);

        IObject desc = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                "{'name': 'The test', 'chainName': 'chainToTest', 'intercept': {}, 'assert': []}".replace('\'','"'));

        desc.setValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "environment"), env);

        new TestEnvironmentHandler().handle(desc, chainNameMock, callbackMock);
        fail();
    }

    @Test(expected = EnvironmentHandleException.class)
    public void Should_throwWhenDescriptionContainsFieldOfUnexpectedType()
            throws Exception {
        IObject desc = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                "{'name': 'The test', 'chainName': 'chainToTest', 'assert': [], 'environment': []}".replace('\'','"'));

        new TestEnvironmentHandler().handle(desc, chainNameMock, callbackMock);
        fail();
    }

    @Test (expected = InitializationException.class)
    public void Should_throwWhenIOCNotInitialized()
            throws Exception {
        IStrategy strategy = mock(IStrategy.class);
        IOC.register(IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), strategy);
        doThrow(Exception.class).when(strategy).resolve(any());
        new TestEnvironmentHandler().handle(mock(IObject.class), chainNameMock, callbackMock);
        fail();
    }
}
