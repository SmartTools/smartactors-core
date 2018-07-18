package info.smart_tools.smartactors.endpoint_components_generic.send_internal_message_handler;

import info.smart_tools.smartactors.base.interfaces.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.base.interfaces.iresolve_dependency_strategy.exception.ResolveDependencyStrategyException;
import info.smart_tools.smartactors.endpoint_components_generic.default_message_context_implementation.DefaultMessageContextImplementation;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IDefaultMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandlerCallback;
import info.smart_tools.smartactors.helpers.trivial_plugins_loading_test_base.TrivialPluginsLoadingTestBase;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessingSequence;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IReceiverChain;
import info.smart_tools.smartactors.task.interfaces.iqueue.IQueue;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

public class SendInternalMessageHandlerTest extends TrivialPluginsLoadingTestBase {
    private IReceiverChain receiverChainMock = mock(IReceiverChain.class);
    private IQueue taskQueue = mock(IQueue.class);

    private IMessageProcessor messageProcessorMock;
    private IMessageProcessingSequence messageProcessingSequenceMock;

    private IResolveDependencyStrategy processorStrategy, sequenceStrategy;

    private IMessageHandlerCallback callback;
    private IDefaultMessageContext messageContext;

    @Override
    protected void registerMocks() throws Exception {
        messageProcessorMock = mock(IMessageProcessor.class);
        messageProcessingSequenceMock = mock(IMessageProcessingSequence.class);

        callback = mock(IMessageHandlerCallback.class);
        messageContext = new DefaultMessageContextImplementation();

        processorStrategy = mock(IResolveDependencyStrategy.class);
        sequenceStrategy = mock(IResolveDependencyStrategy.class);

        when(sequenceStrategy.resolve(eq(13), same(receiverChainMock)))
                .thenReturn(messageProcessingSequenceMock)
                .thenThrow(ResolveDependencyStrategyException.class);
        when(processorStrategy.resolve(same(taskQueue), same(messageProcessingSequenceMock)))
                .thenReturn(messageProcessorMock)
                .thenThrow(ResolveDependencyStrategyException.class);

        IObject env = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()));
        env.setValue(
                IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "message"),
                IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName())));
        env.setValue(
                IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "context"),
                IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName())));

        IOC.register(Keys.getOrAdd(IMessageProcessingSequence.class.getCanonicalName()), sequenceStrategy);
        IOC.register(Keys.getOrAdd(IMessageProcessor.class.getCanonicalName()), processorStrategy);

        messageContext.setDstMessage(env);
    }

    @Test public void Should_createMessageProcessorAndSendMessage() throws Exception {
        new SendInternalMessageHandler(13, taskQueue, receiverChainMock)
                .handle(callback, messageContext);

        verifyNoMoreInteractions(callback);

        verify(messageProcessorMock).process(
                (IObject) same(((IObject) messageContext.getDstMessage())
                        .getValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "message"))),
                (IObject) same(((IObject) messageContext.getDstMessage())
                        .getValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "context")))
        );
    }
}
