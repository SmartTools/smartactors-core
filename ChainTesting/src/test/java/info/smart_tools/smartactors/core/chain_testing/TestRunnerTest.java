package info.smart_tools.smartactors.core.chain_testing;

import info.smart_tools.smartactors.core.chain_testing.exceptions.InvalidTestDescriptionException;
import info.smart_tools.smartactors.core.chain_testing.exceptions.TestStartupException;
import info.smart_tools.smartactors.core.iaction.IAction;
import info.smart_tools.smartactors.core.ichain_storage.IChainStorage;
import info.smart_tools.smartactors.core.ichain_storage.exceptions.ChainNotFoundException;
import info.smart_tools.smartactors.core.ifield_name.IFieldName;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.iqueue.IQueue;
import info.smart_tools.smartactors.core.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.core.itask.ITask;
import info.smart_tools.smartactors.core.message_processing.IMessageProcessingSequence;
import info.smart_tools.smartactors.core.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.core.message_processing.IReceiverChain;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.core.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.plugin.dsobject.PluginDSObject;
import info.smart_tools.smartactors.plugin.ifieldname.IFieldNamePlugin;
import info.smart_tools.smartactors.plugin.ioc_keys.PluginIOCKeys;
import info.smart_tools.smartactors.plugin.scope_provider.PluginScopeProvider;
import info.smart_tools.smartactors.plugin.scoped_ioc.ScopedIOCPlugin;
import info.smart_tools.smartactors.testing.helpers.plugins_loading_test_base.PluginsLoadingTestBase;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test for {@link TestRunner}.
 */
public class TestRunnerTest extends PluginsLoadingTestBase {
    private IObject messageMock;
    private IObject contextMock;
    private IMessageProcessor messageProcessorMock;
    private IQueue<ITask> taskQueueMock;
    private IChainStorage chainStorageMock;
    private IReceiverChain testedChainMock;
    private MainTestChain mainTestChainMock;
    private IMessageProcessingSequence sequenceMock;
    private IAction<Throwable> callbackMock;

    private IResolveDependencyStrategy chainIdStrategyMock;
    private IResolveDependencyStrategy mainTestChainStrategyMock;
    private IResolveDependencyStrategy sequenceStrategyMock;
    private IResolveDependencyStrategy mpStrategyMock;

    private ArgumentCaptor<IAction> cbCaptor = ArgumentCaptor.forClass(IAction.class);

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
        chainStorageMock = mock(IChainStorage.class);
        testedChainMock = mock(IReceiverChain.class);
        mainTestChainMock = mock(MainTestChain.class);
        sequenceMock = mock(IMessageProcessingSequence.class);
        callbackMock = mock(IAction.class);

        when(messageProcessorMock.getSequence()).thenReturn(sequenceMock);

        chainIdStrategyMock = mock(IResolveDependencyStrategy.class);
        mainTestChainStrategyMock = mock(IResolveDependencyStrategy.class);
        sequenceStrategyMock = mock(IResolveDependencyStrategy.class);
        mpStrategyMock = mock(IResolveDependencyStrategy.class);

        when(chainStorageMock.resolve(eq("chainToTest_id"))).thenReturn(testedChainMock);
        when(chainIdStrategyMock.resolve(eq("chainToTest"))).thenReturn("chainToTest_id");
        when(mpStrategyMock.resolve(same(taskQueueMock), same(sequenceMock))).thenReturn(messageProcessorMock);
        when(sequenceStrategyMock.resolve(any(), same(mainTestChainMock))).thenReturn(sequenceMock);
        when(mainTestChainStrategyMock.resolve(cbCaptor.capture(), any())).thenReturn(mainTestChainMock);

        IOC.register(Keys.getOrAdd("task_queue"), new SingletonStrategy(taskQueueMock));
        IOC.register(Keys.getOrAdd(IChainStorage.class.getCanonicalName()), new SingletonStrategy(chainStorageMock));
        IOC.register(Keys.getOrAdd("chain_id_from_map_name"), chainIdStrategyMock);
        IOC.register(Keys.getOrAdd(MainTestChain.class.getCanonicalName()), mainTestChainStrategyMock);
        IOC.register(Keys.getOrAdd(IMessageProcessingSequence.class.getCanonicalName()), sequenceStrategyMock);
        IOC.register(Keys.getOrAdd(IMessageProcessor.class.getCanonicalName()), mpStrategyMock);
    }

    @Test(expected = InvalidArgumentException.class)
    public void Should_throwWhenDescriptionIsNull()
            throws Exception {
        new TestRunner().runTest(null, mock(IAction.class));
    }

    @Test(expected = InvalidArgumentException.class)
    public void Should_throwWhenCallbackIsNull()
            throws Exception {
        new TestRunner().runTest(mock(IObject.class), null);
    }

    @Test
    public void Should_runTest()
            throws Exception {
        IObject env = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()));
        env.setValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "message"), messageMock);
        env.setValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "context"), contextMock);

        IObject desc = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()),
                "{'name': 'The test', 'chainName': 'chainToTest', 'assert': []}".replace('\'','"'));

        desc.setValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "environment"), env);

        new TestRunner().runTest(desc, callbackMock);

        cbCaptor.getAllValues().get(0).execute(null);
        verify(callbackMock).execute(null);
    }

    @Test
    public void Should_runTestWhenItFails()
            throws Exception {
        IObject env = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()));
        env.setValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "message"), messageMock);
        env.setValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "context"), contextMock);

        IObject desc = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()),
                "{'name': 'The test', 'chainName': 'chainToTest', 'assert': []}".replace('\'','"'));

        desc.setValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "environment"), env);

        new TestRunner().runTest(desc, callbackMock);

        cbCaptor.getAllValues().get(0).execute(new Exception());
        verify(callbackMock).execute(any());
    }

    @Test(expected = InvalidTestDescriptionException.class)
    public void Should_throwWhenDescriptionContainsFieldOfUnexpectedType()
            throws Exception {
        IObject desc = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()),
                "{'name': 'The test', 'chainName': 'chainToTest', 'assert': [], 'environment': []}".replace('\'','"'));

        new TestRunner().runTest(desc, callbackMock);
    }

    @Test(expected = TestStartupException.class)
    public void Should_throwWhenCannotResolveChain()
            throws Exception {
        IObject env = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()));
        env.setValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "message"), messageMock);
        env.setValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "context"), contextMock);

        IObject desc = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()),
                "{'name': 'The test', 'chainName': 'nonexist', 'assert': []}".replace('\'','"'));

        desc.setValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "environment"), env);

        when(chainIdStrategyMock.resolve(eq("nonexist"))).thenReturn("nonexist_id");
        when(chainStorageMock.resolve(eq("nonexist_id"))).thenThrow(ChainNotFoundException.class);

        new TestRunner().runTest(desc, callbackMock);
    }
}
