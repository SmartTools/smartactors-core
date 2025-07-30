package info.smart_tools.smartactors.message_bus.message_bus_handler;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.helpers.IOCInitializer.IOCInitializer;
import info.smart_tools.smartactors.iobject.field_name.FieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.istrategy_container.IStrategyContainer;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.ioc.resolve_by_name_ioc_with_lambda_strategy.ResolveByNameIocStrategy;
import info.smart_tools.smartactors.ioc.strategy_container.StrategyContainer;
import info.smart_tools.smartactors.ioc.string_ioc_key.Key;
import info.smart_tools.smartactors.message_bus.interfaces.imessage_bus_handler.IMessageBusHandler;
import info.smart_tools.smartactors.message_bus.interfaces.imessage_bus_handler.exception.MessageBusHandlerException;
import info.smart_tools.smartactors.message_processing_interfaces.ichain_storage.IChainStorage;
import info.smart_tools.smartactors.message_processing_interfaces.iresponse_strategy.IResponseStrategy;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessingSequence;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IReceiverChain;
import info.smart_tools.smartactors.scope.iscope.IScope;
import info.smart_tools.smartactors.scope.scope_provider.ScopeProvider;
import info.smart_tools.smartactors.task.interfaces.iqueue.IQueue;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link MessageBusHandler}.
 */
public class MessageBusHandlerTest extends IOCInitializer {

    private IStrategyContainer container = new StrategyContainer();
    private IQueue queue = mock(IQueue.class);
//    private IReceiverChain chain = mock(IReceiverChain.class);
    private Object chain = "chainName";
    private IResponseStrategy nullResponseStrategy;
    private IResponseStrategy mbResponseStrategy;

    @Override
    protected void registry(String... strategyNames) throws Exception {
        registryStrategies("ifieldname strategy", "iobject strategy");
    }

    @Override
    protected void registerMocks() throws Exception {
        nullResponseStrategy = mock(IResponseStrategy.class);
        mbResponseStrategy = mock(IResponseStrategy.class);

        IOC.register(Keys.getKeyByName("null response strategy"), new SingletonStrategy(nullResponseStrategy));
        IOC.register(Keys.getKeyByName("message bus response strategy"), new SingletonStrategy(mbResponseStrategy));
    }

    @Test
    public void checkCreation()
            throws Exception {
        IMessageBusHandler handler = new MessageBusHandler(this.queue, 1, this.chain, mock(IAction.class));
        assertNotNull(handler);
    }

    @Test (expected = InvalidArgumentException.class)
    public void checkInvalidArgumentExceptionOnNullQueue() throws Exception {
        IMessageBusHandler handler = new MessageBusHandler(null, 1, this.chain, mock(IAction.class));
        fail();
    }

    @Test (expected = InvalidArgumentException.class)
    public void checkInvalidArgumentExceptionOnWrongStackDepth() throws Exception {
        IMessageBusHandler handler = new MessageBusHandler(this.queue, -1, this.chain, mock(IAction.class));
        fail();
    }

    @Test (expected = InvalidArgumentException.class)
    public void checkInvalidArgumentExceptionOnNullReceiverChain() throws Exception {
        IMessageBusHandler handler = new MessageBusHandler(this.queue, 1, null, mock(IAction.class));
        fail();
    }

    @Test (expected = InvalidArgumentException.class)
    public void checkInvalidArgumentExceptionOnNullFinalAction() throws Exception {
        IMessageBusHandler handler = new MessageBusHandler(this.queue, 1, this.chain, null);
        fail();
    }

    @Test
    public void checkMessageHandle()
            throws Exception {
        IStrategy sequenceStrategy = mock(IStrategy.class);
        IOC.register(
                IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessingSequence"),
                sequenceStrategy
        );
        IStrategy messageProcessorStrategy = mock(IStrategy.class);
        IOC.register(
                IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor"),
                messageProcessorStrategy
        );
        IStrategy iobjectStrategy = mock(IStrategy.class);
        IOC.register(
                IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.iobject.IObject"),
                iobjectStrategy
        );
        IMessageProcessingSequence sequence = mock(IMessageProcessingSequence.class);
        IMessageProcessor processor = mock(IMessageProcessor.class);
        when(messageProcessorStrategy.resolve(this.queue, sequence)).thenReturn(processor);
        IObject context = mock(IObject.class);
        when(iobjectStrategy.resolve()).thenReturn(context);
        IObject message = mock(IObject.class);
        when(sequenceStrategy.resolve(1, this.chain, message, true)).thenReturn(sequence);

        IMessageBusHandler handler = new MessageBusHandler(this.queue, 1, this.chain, mock(IAction.class));
        handler.handle(message, true);
        verify(context, times(1)).setValue(eq(new FieldName("responseStrategy")), same(nullResponseStrategy));
        verify(sequenceStrategy, times(1)).resolve(1, this.chain, message, true);
        verify(messageProcessorStrategy, times(1)).resolve(this.queue, sequence);
        verify(iobjectStrategy, times(1)).resolve();
        verify(processor, times(1)).process(message, context);
    }

    @Test (expected = MessageBusHandlerException.class)
    public void checkMessageBusHandlerExceptionOnErrorInHandle()
            throws Exception {
        IMessageBusHandler handler = new MessageBusHandler(this.queue, 1, this.chain, mock(IAction.class));
        handler.handle(null, true);
        fail();
    }

    @Test
    public void checkMessageHandleWithSpecificChain()
            throws Exception {
        IStrategy sequenceStrategy = mock(IStrategy.class);
        IOC.register(
                IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessingSequence"),
                sequenceStrategy
        );
        IStrategy messageProcessorStrategy = mock(IStrategy.class);
        IOC.register(
                IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor"),
                messageProcessorStrategy
        );
        IStrategy iobjectStrategy = mock(IStrategy.class);
        IOC.register(
                IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.iobject.IObject"),
                iobjectStrategy
        );
        IStrategy chainIdStrategy = mock(IStrategy.class);
        IOC.register(
                IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "chain_id_from_map_name_and_message"),
                chainIdStrategy
        );
        IStrategy chainStorageStrategy = mock(IStrategy.class);
        IOC.register(
                IOC.resolve(IOC.getKeyForKeyByNameStrategy(), IChainStorage.class.getCanonicalName()),
                chainStorageStrategy
        );
        Object chainName = mock(Object.class);
        IMessageProcessingSequence sequence = mock(IMessageProcessingSequence.class);
        IMessageProcessor processor = mock(IMessageProcessor.class);
        when(messageProcessorStrategy.resolve(this.queue, sequence)).thenReturn(processor);
        IObject context = mock(IObject.class);
        when(iobjectStrategy.resolve()).thenReturn(context);
        IObject message = mock(IObject.class);
        when(sequenceStrategy.resolve(1, chainName, message, true)).thenReturn(sequence);

        IMessageBusHandler handler = new MessageBusHandler(this.queue, 1, this.chain, mock(IAction.class));
        handler.handle(message, chainName, true);
        verify(sequenceStrategy, times(1)).resolve(1, chainName, message, true);
        verify(messageProcessorStrategy, times(1)).resolve(this.queue, sequence);
        verify(iobjectStrategy, times(1)).resolve();
        verify(processor, times(1)).process(message, context);
    }

    @Test (expected = MessageBusHandlerException.class)
    public void checkMessageBusHandlerExceptionOnErrorInHandleWithSpecificChain()
            throws Exception {
        IMessageBusHandler handler = new MessageBusHandler(this.queue, 1, this.chain, mock(IAction.class));
        handler.handle(null, null, true);
        fail();
    }

    @Test
    public void checkMessageHandleWithReply()
            throws Exception {
        IStrategy sequenceStrategy = mock(IStrategy.class);
        IOC.register(
                IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessingSequence"),
                sequenceStrategy
        );
        IStrategy messageProcessorStrategy = mock(IStrategy.class);
        IOC.register(
                IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor"),
                messageProcessorStrategy
        );
        IStrategy iobjectStrategy = mock(IStrategy.class);
        IOC.register(
                IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.iobject.IObject"),
                iobjectStrategy
        );
        IStrategy chainIdStrategy = mock(IStrategy.class);
        IOC.register(
                IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "chain_id_from_map_name_and_message"),
                chainIdStrategy
        );
        IStrategy chainStorageStrategy = mock(IStrategy.class);
        IOC.register(
                IOC.resolve(IOC.getKeyForKeyByNameStrategy(), IChainStorage.class.getCanonicalName()),
                chainStorageStrategy
        );
        IMessageProcessingSequence sequence = mock(IMessageProcessingSequence.class);
        IMessageProcessor processor = mock(IMessageProcessor.class);
        when(messageProcessorStrategy.resolve(this.queue, sequence)).thenReturn(processor);
        IObject context = mock(IObject.class);
        when(iobjectStrategy.resolve()).thenReturn(context);
        IObject message = mock(IObject.class);
        when(sequenceStrategy.resolve(1, this.chain, message, true)).thenReturn(sequence);
        IAction finalAction = mock(IAction.class);
        Object replyToChainName = mock(Object.class);

        Object replyToChainId = mock(Object.class);
        IChainStorage storage = mock(IChainStorage.class);
        IReceiverChain replyToChain = mock(IReceiverChain.class);
        when(chainIdStrategy.resolve(replyToChainName)).thenReturn(replyToChainId);
        when(chainStorageStrategy.resolve()).thenReturn(storage);
        when(storage.resolve(replyToChainId)).thenReturn(replyToChain);

        IMessageBusHandler handler = new MessageBusHandler(this.queue, 1, this.chain, finalAction);
        handler.handleForReply(message, replyToChainName, true);
        verify(sequenceStrategy, times(1)).resolve(1, this.chain, message, true);
        verify(messageProcessorStrategy, times(1)).resolve(this.queue, sequence);
        verify(iobjectStrategy, times(1)).resolve();
        verify(processor, times(1)).process(message, context);
        verify(context, times(1)).setValue(eq(new FieldName("finalActions")), any(List.class));
        verify(context, times(1)).setValue(new FieldName("messageBusReplyTo"), replyToChainName);
        verify(context, times(1)).setValue(eq(new FieldName("responseStrategy")), same(mbResponseStrategy));
    }

    @Test (expected = MessageBusHandlerException.class)
    public void checkMessageBusHandlerExceptionOnErrorInHandleForReply()
            throws Exception {
        IMessageBusHandler handler = new MessageBusHandler(this.queue, 1, this.chain, mock(IAction.class));
        handler.handleForReply(null, null, true);
        fail();
    }

    @Test
    public void checkMessageHandleWithSpecificChainAndReply()
            throws Exception {
        IStrategy sequenceStrategy = mock(IStrategy.class);
        IOC.register(
                IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessingSequence"),
                sequenceStrategy
        );
        IStrategy messageProcessorStrategy = mock(IStrategy.class);
        IOC.register(
                IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor"),
                messageProcessorStrategy
        );
        IStrategy iobjectStrategy = mock(IStrategy.class);
        IOC.register(
                IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.iobject.IObject"),
                iobjectStrategy
        );
        IStrategy chainIdStrategy = mock(IStrategy.class);
        IOC.register(
                IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "chain_id_from_map_name_and_message"),
                chainIdStrategy
        );
        IStrategy chainStorageStrategy = mock(IStrategy.class);
        IOC.register(
                IOC.resolve(IOC.getKeyForKeyByNameStrategy(), IChainStorage.class.getCanonicalName()),
                chainStorageStrategy
        );
        IReceiverChain chain = mock(IReceiverChain.class);
        IMessageProcessingSequence sequence = mock(IMessageProcessingSequence.class);
        IMessageProcessor processor = mock(IMessageProcessor.class);
        when(messageProcessorStrategy.resolve(this.queue, sequence)).thenReturn(processor);
        IObject context = mock(IObject.class);
        when(iobjectStrategy.resolve()).thenReturn(context);
        IObject message = mock(IObject.class);
        IAction finalAction = mock(IAction.class);
        Object replyToChainName = mock(Object.class);
        Object chainName = mock(Object.class);
        when(sequenceStrategy.resolve(1, chainName, message, true)).thenReturn(sequence);

        Object replyToChainId = mock(Object.class);
        Object chainId = mock(Object.class);
        IChainStorage storage = mock(IChainStorage.class);
        IReceiverChain replyToChain = mock(IReceiverChain.class);

        when(chainIdStrategy.resolve(replyToChainName)).thenReturn(replyToChainId);
        when(chainIdStrategy.resolve(chainName)).thenReturn(chainId);
        when(chainStorageStrategy.resolve()).thenReturn(storage);
        when(storage.resolve(replyToChainId)).thenReturn(replyToChain);
        when(storage.resolve(chainId)).thenReturn(chain);

        IMessageBusHandler handler = new MessageBusHandler(this.queue, 1, this.chain, finalAction);
        handler.handleForReply(message, chainName, replyToChainName, true);
        verify(sequenceStrategy, times(1)).resolve(1, chainName, message, true);
        verify(messageProcessorStrategy, times(1)).resolve(this.queue, sequence);
        verify(iobjectStrategy, times(1)).resolve();
        verify(processor, times(1)).process(message, context);
        verify(context, times(1)).setValue(eq(new FieldName("finalActions")), any(List.class));
        verify(context, times(1)).setValue(new FieldName("messageBusReplyTo"), replyToChainName);
    }

    @Test (expected = MessageBusHandlerException.class)
    public void checkMessageBusHandlerExceptionOnErrorInHandleForReplyWithSpecificChain()
            throws Exception {
        IMessageBusHandler handler = new MessageBusHandler(this.queue, 1, this.chain, mock(IAction.class));
        handler.handleForReply(null, null, null, true);
        fail();
    }
}
