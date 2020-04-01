package info.smart_tools.smartactors.checkpoint.checkpoint_actor;

import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.checkpoint.interfaces.IRecoverStrategy;
import info.smart_tools.smartactors.helpers.IOCInitializer.IOCInitializer;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_bus.interfaces.imessage_bus_handler.IMessageBusHandler;
import info.smart_tools.smartactors.message_bus.message_bus.MessageBus;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerEntry;
import info.smart_tools.smartactors.scheduler.interfaces.exceptions.SchedulerActionInitializationException;
import info.smart_tools.smartactors.scope.scope_provider.ScopeProvider;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

/**
 * Tst for {@link CheckpointSchedulerAction}.
 */
public class CheckpointSchedulerActionTest extends IOCInitializer {
    private IRecoverStrategy recoverStrategy;
    private IAction failureAction;
    private ISchedulerEntry entryMock;
    private IObject entryState;
    private String entryId;
    private IMessageBusHandler messageBusHandlerMock;
    private Object chainId = new Object();

    @Override
    protected void registry(String... strategyNames) throws Exception {
        registryStrategies("ifieldname strategy", "iobject strategy");
    }

    @Override
    protected void registerMocks() throws Exception {
        recoverStrategy = mock(IRecoverStrategy.class);
        IOC.register(Keys.getKeyByName("the recover strategy"), new SingletonStrategy(recoverStrategy));

        failureAction = mock(IAction.class);
        IOC.register(Keys.getKeyByName("checkpoint failure action"), new SingletonStrategy(failureAction));

        entryMock = mock(ISchedulerEntry.class);
        entryState = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"));
        entryId = UUID.randomUUID().toString();
        when(entryMock.getState()).thenReturn(entryState);
        when(entryMock.getId()).thenReturn(entryId);

        messageBusHandlerMock = mock(IMessageBusHandler.class);
        ScopeProvider.getCurrentScope().setValue(MessageBus.getMessageBusKey(), messageBusHandlerMock);
    }

    @Test(expected = SchedulerActionInitializationException.class)
    public void Should_throwWhenInitializationArgumentsDoNotContainMessage()
            throws Exception {
        CheckpointSchedulerAction action = new CheckpointSchedulerAction();

        IObject args = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                "{'recover':{'strategy':'the recover strategy'},'message':null}".replace('\'','"'));

        action.init(entryMock, args);
    }

    @Test
    public void Should_initializeRecoverStrategyAndCopyFieldsToEntryStateOnInitialization()
            throws Exception {
        CheckpointSchedulerAction action = new CheckpointSchedulerAction();
        IMessageProcessor processorMock = mock(IMessageProcessor.class);

        IObject args = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                ("{'recover':{'strategy':'the recover strategy'}," +
                        "'message':{'a':'1'}," +
                        "'responsibleCheckpointId':'rCP'," +
                        "'prevCheckpointId':'prCP'," +
                        "'prevCheckpointEntryId':'pcpEi'}").replace('\'','"'));

        args.setValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "processor"), processorMock);

        action.init(entryMock, args);

        verify(recoverStrategy).init(same(entryState), any(), same(processorMock));
        assertEquals("rCP", entryState.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "responsibleCheckpointId")));
        assertEquals("prCP", entryState.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "prevCheckpointId")));
        assertEquals("pcpEi", entryState.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "prevCheckpointEntryId")));
        assertEquals("the recover strategy", entryState.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "recoverStrategy")));
        assertEquals("1", ((IObject) entryState.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "message")))
            .getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "a")));
    }

    @Test
    public void Should_sendCloneOfMessageFillingCheckpointStatusOnExecution()
            throws Exception {
        IObject entryState = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                ("{'recoverStrategy':'the recover strategy'," +
                        "'message':{'a':'1'}," +
                        "'responsibleCheckpointId':'rCP'," +
                        "'prevCheckpointId':'prCP'," +
                        "'prevCheckpointEntryId':'pcpEi'}").replace('\'','"'));

        when(entryMock.getState()).thenReturn(entryState);

        CheckpointSchedulerAction action = new CheckpointSchedulerAction();

        action.execute(entryMock);

        verify(recoverStrategy).reSend(same(entryState));

//        ArgumentCaptor<IObject> mc = ArgumentCaptor.forClass(IObject.class);
//
//        verify(messageBusHandlerMock).handle(mc.capture(), same(chainId));
//
//        IObject sent = mc.getValue();
//        IObject sentCPS = (IObject) sent.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "checkpointStatus"));
//
//        assertEquals("1", sent.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "a")));
//
//        assertEquals(entryId, sentCPS.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "checkpointEntryId")));
//        assertEquals("rCP", sentCPS.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "responsibleCheckpointId")));
//        assertEquals("prCP", sentCPS.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "prevCheckpointId")));
//        assertEquals("pcpEi", sentCPS.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "prevCheckpointEntryId")));
//
//        assertNotSame(sent, entryState.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "message")));
    }

    @Test
    public void Should_notSendMessageIfEntryIsMarkedAsCompleted()
            throws Exception {
        IObject entryState = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                ("{'recoverStrategy':'the recover strategy'," +
                        "'message':{'a':'1'}," +
                        "'responsibleCheckpointId':'rCP'," +
                        "'prevCheckpointId':'prCP'," +
                        "'prevCheckpointEntryId':'pcpEi'," +
                        "'completed':true," +
                        "'gotFeedback':true}").replace('\'','"'));

        when(entryMock.getState()).thenReturn(entryState);

        CheckpointSchedulerAction action = new CheckpointSchedulerAction();

        action.execute(entryMock);

        verify(messageBusHandlerMock, never()).handle(any(), any(), eq(true));
    }

    @Test
    public void Should_notExecuteFailureActionIfEntryIsMarkedAsCompletedButGotNoFeedback()
            throws Exception {
        IObject entryState = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                ("{'recoverStrategy':'the recover strategy'," +
                        "'message':{'a':'1'}," +
                        "'responsibleCheckpointId':'rCP'," +
                        "'prevCheckpointId':'prCP'," +
                        "'prevCheckpointEntryId':'pcpEi'," +
                        "'completed':true}").replace('\'','"'));

        when(entryMock.getState()).thenReturn(entryState);

        CheckpointSchedulerAction action = new CheckpointSchedulerAction();

        action.execute(entryMock);

        verify(messageBusHandlerMock, never()).handle(any(), any(), eq(true));
        verify(failureAction).execute(same(entryState.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "message"))));
    }
}
