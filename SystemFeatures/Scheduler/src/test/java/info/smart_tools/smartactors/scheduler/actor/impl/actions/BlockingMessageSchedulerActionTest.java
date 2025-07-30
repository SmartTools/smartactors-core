package info.smart_tools.smartactors.scheduler.actor.impl.actions;

import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.interfaces.istrategy.exception.StrategyException;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.helpers.IOCInitializer.IOCInitializer;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.ichain_storage.IChainStorage;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessingSequence;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IReceiverChain;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.MessageProcessorProcessException;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerAction;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerEntry;
import info.smart_tools.smartactors.scheduler.interfaces.exceptions.SchedulerActionExecutionException;
import info.smart_tools.smartactors.scheduler.interfaces.exceptions.SchedulerActionInitializationException;
import info.smart_tools.smartactors.task.interfaces.iqueue.IQueue;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

/**
 * Test for {@link BlockingMessageSchedulerAction}.
 */
public class BlockingMessageSchedulerActionTest extends IOCInitializer {
    private IMessageProcessor messageProcessorMock;
    private IMessageProcessingSequence messageProcessingSequenceMock;
    private IChainStorage chainStorageMock;
    private IReceiverChain receiverChainMock;
    private ISchedulerEntry schedulerEntryMock;
    private IQueue queueMock;
    private IObject entryState;

    private IStrategy sequenceStrategy, processorStrategy;

    private ISchedulerAction action;

    @Override
    protected void registry(String... strategyNames) throws Exception {
        registryStrategies("ifieldname strategy", "iobject strategy");
    }

    @Override
    protected void registerMocks() throws Exception {
        messageProcessorMock = mock(IMessageProcessor.class);
        messageProcessingSequenceMock = mock(IMessageProcessingSequence.class);
        chainStorageMock = mock(IChainStorage.class);
        receiverChainMock = mock(IReceiverChain.class);
        schedulerEntryMock = mock(ISchedulerEntry.class);
        queueMock = mock(IQueue.class);

        entryState = IOC.resolve(Keys.getKeyByName(IObject.class.getCanonicalName()));

        when(schedulerEntryMock.getState()).thenReturn(entryState);

        sequenceStrategy = mock(IStrategy.class);
        when(sequenceStrategy.resolve(any(), same(receiverChainMock)))
                .thenReturn(messageProcessingSequenceMock)
                .thenThrow(StrategyException.class);
        IOC.register(Keys.getKeyByName(IMessageProcessingSequence.class.getCanonicalName()), sequenceStrategy);
        processorStrategy = mock(IStrategy.class);
        when(processorStrategy.resolve(same(queueMock), same(messageProcessingSequenceMock)))
                .thenReturn(messageProcessorMock)
                .thenThrow(StrategyException.class);
        IOC.register(Keys.getKeyByName(IMessageProcessor.class.getCanonicalName()), processorStrategy);

        when(schedulerEntryMock.getId()).thenReturn(UUID.randomUUID().toString());

        IOC.register(Keys.getKeyByName(IChainStorage.class.getCanonicalName()), new SingletonStrategy(chainStorageMock));
        IOC.register(Keys.getKeyByName("default_stack_depth"), new SingletonStrategy(321));
        IOC.register(Keys.getKeyByName("task_queue"), new SingletonStrategy(queueMock));

        IOC.register(Keys.getKeyByName("chain_id_from_map_name_and_message"), new IStrategy() {
            @Override
            public <T> T resolve(Object... args) throws StrategyException {
                return (T) (String.valueOf(args[0]) + "__id");
            }
        });

        when(chainStorageMock.resolve(eq("some_chain__id"))).thenReturn(receiverChainMock);

        action = new BlockingMessageSchedulerAction();
    }

    private IFieldName fn(String n) throws Exception {
        return IOC.resolve(Keys.getKeyByName(IFieldName.class.getCanonicalName()), n);
    }

    @Test
    public void Should_initializeEntryState()
            throws Exception {
        IObject args = IOC.resolve(Keys.getKeyByName(IObject.class.getCanonicalName()),
                ("{" +
                        "'message': {'a':'asd'}," +
                        "'setEntryId': 'idFld'," +
                        "'preShutdownExec': true," +
                        "'chain': 'some_chain'," +
                        "'stackDepth': 13" +
                        "}").replace('\'','"'));
        action.init(schedulerEntryMock, args);

        assertEquals("asd", ((IObject)entryState.getValue(fn("message"))).getValue(fn("a")));
        assertEquals(schedulerEntryMock.getId(), ((IObject)entryState.getValue(fn("message"))).getValue(fn("idFld")));
        assertEquals(Boolean.TRUE, entryState.getValue(fn("preShutdownExec")));
        assertEquals("some_chain", entryState.getValue(fn("chain")));
        assertEquals(13, entryState.getValue(fn("stackDepth")));
    }

    @Test(expected = SchedulerActionInitializationException.class)
    public void Should_throwWhenNoMessageGiven()
            throws Exception {
        IObject args = IOC.resolve(Keys.getKeyByName(IObject.class.getCanonicalName()),
                ("{" +
                        "'setEntryId': 'idFld'," +
                        "'preShutdownExec': true," +
                        "'chain': 'some_chain'," +
                        "'stackDepth': 13" +
                        "}").replace('\'','"'));
        action.init(schedulerEntryMock, args);
    }

    @Test(expected = SchedulerActionInitializationException.class)
    public void Should_throwWhenNoChainGiven()
            throws Exception {
        IObject args = IOC.resolve(Keys.getKeyByName(IObject.class.getCanonicalName()),
                ("{" +
                        "'message': {'a':'asd'}," +
                        "'setEntryId': 'idFld'," +
                        "'preShutdownExec': true," +
                        "'stackDepth': 13" +
                        "}").replace('\'','"'));
        action.init(schedulerEntryMock, args);
    }

    @Test(expected = SchedulerActionInitializationException.class)
    public void Should_throwWhenErrorOccursResolvingDependency()
            throws Exception {
        IOC.unregister(Keys.getKeyByName("default_stack_depth"));
        IObject args = IOC.resolve(Keys.getKeyByName(IObject.class.getCanonicalName()),
                ("{" +
                        "'message': {'a':'asd'}," +
                        "'setEntryId': 'idFld'," +
                        "'preShutdownExec': true," +
                        "'chain': 'some_chain'" +
                        "}").replace('\'','"'));
        action.init(schedulerEntryMock, args);
    }

    @Test
    public void Should_useDefaultWhenNoStackDepthGiven()
            throws Exception {
        IObject args = IOC.resolve(Keys.getKeyByName(IObject.class.getCanonicalName()),
                ("{" +
                        "'message': {'a':'asd'}," +
                        "'setEntryId': 'idFld'," +
                        "'preShutdownExec': true," +
                        "'chain': 'some_chain'" +
                        "}").replace('\'','"'));
        action.init(schedulerEntryMock, args);
        assertEquals(321, entryState.getValue(fn("stackDepth")));
    }

    @Test
    public void Should_sendMessageAndAddFinalActionToContext()
            throws Exception {
        IObject args = IOC.resolve(Keys.getKeyByName(IObject.class.getCanonicalName()),
                ("{" +
                        "'message': {'a':'asd'}," +
                        "'setEntryId': 'idFld'," +
                        "'preShutdownExec': true," +
                        "'chain': 'some_chain'," +
                        "'stackDepth': 13" +
                        "}").replace('\'','"'));
        action.init(schedulerEntryMock, args);

        action.execute(schedulerEntryMock);

        ArgumentCaptor<IObject> messageCaptor = ArgumentCaptor.forClass(IObject.class);
        ArgumentCaptor<IObject> contextCaptor = ArgumentCaptor.forClass(IObject.class);

        verify(messageProcessorMock).process(messageCaptor.capture(), contextCaptor.capture());

        assertNotSame(args.getValue(fn("message")), messageCaptor.getValue());
        assertNotSame(entryState.getValue(fn("message")), messageCaptor.getValue());
        assertEquals("asd", messageCaptor.getValue().getValue(fn("a")));

        verify(schedulerEntryMock).pause();
        verify(schedulerEntryMock, times(0)).unpause();

        for (IAction<IObject> fa : (List<IAction<IObject>>) contextCaptor.getValue().getValue(fn("finalActions"))) {
            fa.execute(null);
        }

        verify(schedulerEntryMock, times(1)).unpause();
    }

    @Test
    public void Should_unpauseEntryImmediatelyIfMessageProcessorFailsToStart()
            throws Exception {
        IObject args = IOC.resolve(Keys.getKeyByName(IObject.class.getCanonicalName()),
                ("{" +
                        "'message': {'a':'asd'}," +
                        "'setEntryId': 'idFld'," +
                        "'preShutdownExec': true," +
                        "'chain': 'some_chain'," +
                        "'stackDepth': 13" +
                        "}").replace('\'','"'));
        action.init(schedulerEntryMock, args);

        doThrow(MessageProcessorProcessException.class).when(messageProcessorMock).process(any(), any());

        try {
            action.execute(schedulerEntryMock);
            fail();
        } catch (SchedulerActionExecutionException ok) { }

        verify(schedulerEntryMock).pause();
        verify(schedulerEntryMock).unpause();
    }
}
