package info.smart_tools.smartactors.endpoint_components_generic.respond_to_chain_client_callback;

import info.smart_tools.smartactors.base.interfaces.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.base.interfaces.iresolve_dependency_strategy.exception.ResolveDependencyStrategyException;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.endpoint_interfaces.iclient_callback.IClientCallback;
import info.smart_tools.smartactors.endpoint_interfaces.iclient_callback.exceptions.ClientCallbackException;
import info.smart_tools.smartactors.helpers.trivial_plugins_loading_test_base.TrivialPluginsLoadingTestBase;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.ichain_storage.IChainStorage;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessingSequence;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IReceiverChain;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class RespondToChainClientCallbackTest extends TrivialPluginsLoadingTestBase {
    private IResolveDependencyStrategy sequenceStrategyMock;
    private IResolveDependencyStrategy processorStrategyMock;

    private IMessageProcessingSequence sequenceMock;
    private IMessageProcessor processorMock;

    private IChainStorage chainStorageMock;
    private IReceiverChain successChainMock;
    private IReceiverChain errorChainMock;

    private Object taskQueue = new Object();

    private ArgumentCaptor<IObject> iObjectArgumentCaptor1, iObjectArgumentCaptor2;

    @Override
    protected void registerMocks() throws Exception {
        sequenceStrategyMock = mock(IResolveDependencyStrategy.class);
        processorStrategyMock = mock(IResolveDependencyStrategy.class);

        sequenceMock = mock(IMessageProcessingSequence.class);
        processorMock = mock(IMessageProcessor.class);

        chainStorageMock = mock(IChainStorage.class);
        successChainMock = mock(IReceiverChain.class);
        errorChainMock = mock(IReceiverChain.class);

        when(sequenceStrategyMock.resolve(anyVararg()))
                .thenReturn(sequenceMock).thenThrow(new ResolveDependencyStrategyException("Called twice"));
        when(processorStrategyMock.resolve(anyVararg()))
                .thenReturn(processorMock).thenThrow(new ResolveDependencyStrategyException("Called twice"));

        when(chainStorageMock.resolve(eq("success_chain__0"))).thenReturn(successChainMock);
        when(chainStorageMock.resolve(eq("error_chain__0"))).thenReturn(errorChainMock);

        IOC.register(Keys.getOrAdd("chain_id_from_map_name"), new ApplyFunctionToArgumentsStrategy(args -> args[0] + "__0"));
        IOC.register(Keys.getOrAdd(IMessageProcessingSequence.class.getCanonicalName()), sequenceStrategyMock);
        IOC.register(Keys.getOrAdd(IMessageProcessor.class.getCanonicalName()), processorStrategyMock);
        IOC.register(Keys.getOrAdd(IChainStorage.class.getCanonicalName()), new SingletonStrategy(chainStorageMock));
        IOC.register(Keys.getOrAdd("task_queue"), new SingletonStrategy(taskQueue));

        iObjectArgumentCaptor1 = ArgumentCaptor.forClass(IObject.class);
        iObjectArgumentCaptor2 = ArgumentCaptor.forClass(IObject.class);
    }

    @Test(expected = ClientCallbackException.class)
    public void Should_throwWhenNoSuccessChainProvided() throws Exception {
        IObject env = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()),
                "{'errorChain': 'error_chain'}".replace('\'','"'));
        new RespondToChainClientCallback().onStart(env);
    }

    @Test(expected = ClientCallbackException.class)
    public void Should_throwWhenNoErrorChainProvided() throws Exception {
        IObject env = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()),
                "{'successChain': 'success_chain'}".replace('\'','"'));
        new RespondToChainClientCallback().onStart(env);
    }

    @Test
    public void Should_sendMessageToErrorChain() throws Exception {
        IObject env = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()),
                ("{" +
                        "'errorChain': 'error_chain'," +
                        "'successChain': 'success_chain'" +
                        "}").replace('\'','"'));
        Throwable err = new Exception();

        IClientCallback callback = new RespondToChainClientCallback();

        callback.onStart(env);
        callback.onError(env, err);

        verify(sequenceStrategyMock).resolve(any(), same(errorChainMock));
        verify(processorStrategyMock).resolve(same(taskQueue), same(sequenceMock));

        verify(processorMock, times(1)).process(any(), iObjectArgumentCaptor1.capture());
        assertSame(
                env,
                iObjectArgumentCaptor1.getValue()
                    .getValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "request"))
        );
        assertSame(
                err,
                iObjectArgumentCaptor1.getValue()
                    .getValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "exception"))
        );
    }

    @Test
    public void Should_sendMessageToSuccessChain() throws Exception {
        IObject env = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()),
                ("{" +
                        "'errorChain': 'error_chain'," +
                        "'successChain': 'success_chain'" +
                        "}").replace('\'','"'));
        IObject res = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()),
                ("{" +
                        "'message':{}" +
                        "}").replace('\'','"'));

        IClientCallback callback = new RespondToChainClientCallback();

        callback.onStart(env);
        callback.onSuccess(env, res);

        verify(sequenceStrategyMock).resolve(any(), same(successChainMock));
        verify(processorStrategyMock).resolve(same(taskQueue), same(sequenceMock));

        verify(processorMock, times(1))
                .process(iObjectArgumentCaptor1.capture(), iObjectArgumentCaptor2.capture());
        assertSame(
                res.getValue(IOC
                        .resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "message")),
                iObjectArgumentCaptor1.getValue()
        );
        assertSame(
                res,
                iObjectArgumentCaptor2.getValue()
        );
    }
}
