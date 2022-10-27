package info.smart_tools.smartactors.checkpoint.checkpoint_actor;

import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.interfaces.istrategy.exception.StrategyException;
import info.smart_tools.smartactors.base.isynchronous_service.exceptions.IllegalServiceStateException;
import info.smart_tools.smartactors.base.isynchronous_service.exceptions.ServiceStopException;
import info.smart_tools.smartactors.base.iup_counter.IUpCounter;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.checkpoint.checkpoint_actor.wrappers.EnteringMessage;
import info.smart_tools.smartactors.checkpoint.checkpoint_actor.wrappers.FeedbackMessage;
import info.smart_tools.smartactors.checkpoint.checkpoint_actor.wrappers.StartStopMessage;
import info.smart_tools.smartactors.helpers.IOCInitializer.IOCInitializer;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_bus.interfaces.imessage_bus_handler.IMessageBusHandler;
import info.smart_tools.smartactors.message_bus.message_bus.MessageBus;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessingSequence;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerEntry;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerEntryStorage;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerEntryStorageObserver;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerService;
import info.smart_tools.smartactors.scheduler.interfaces.exceptions.EntryNotFoundException;
import info.smart_tools.smartactors.scope.scope_provider.ScopeProvider;
import info.smart_tools.smartactors.task.interfaces.iqueue.IQueue;
import info.smart_tools.smartactors.task.interfaces.itask.ITask;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Test for {@link CheckpointActor}.
 */
public class CheckpointActorTest extends IOCInitializer {
    private final Object connectionPool = new Object();
    private final Object connectionOptions = new Object();

    private IQueue taskQueueMock;
    private ISchedulerEntryStorage storageMock;
    private ISchedulerService serviceMock;
    private IAction activationActionMock;
    private IMessageBusHandler messageBusHandlerMock;

    private ISchedulerEntryStorageObserver observer;

    private final String collectionName = "the_collection_name";

    private IStrategy newEntryStrategyMock;
    private ISchedulerEntry entryMock[];

    private EnteringMessage enteringMessageMock;
    private FeedbackMessage feedbackMessageMock;

    private ArgumentCaptor<Object> objectsArgumentCaptor;
    private ArgumentCaptor<IObject> iObjectArgumentCaptor;

    private IMessageProcessor messageProcessorMock;
    private IMessageProcessingSequence messageProcessingSequenceMock;

    private IUpCounter upCounterMock;

    @Override
    protected void registry(String... strategyNames) throws Exception {
        registryStrategies("ifieldname strategy", "iobject strategy");
    }

    @Override
    protected void registerMocks() throws Exception {
        taskQueueMock = mock(IQueue.class);
        doAnswer(invocation -> {
            invocation.getArgumentAt(0, ITask.class).execute();
            return null;
        }).when(taskQueueMock).put(any());
        IOC.register(Keys.getKeyByName("task_queue"), new SingletonStrategy(taskQueueMock));

        IOC.register(Keys.getKeyByName("the connection options"), new SingletonStrategy(connectionOptions));
        IOC.register(Keys.getKeyByName("the connection pool"), new SingletonStrategy(connectionPool));

        storageMock = mock(ISchedulerEntryStorage.class);
        serviceMock = mock(ISchedulerService.class);
        when(serviceMock.getEntryStorage()).thenReturn(storageMock);

        IOC.register(Keys.getKeyByName("new scheduler service"), new IStrategy() {
            @Override
            public <T> T resolve(Object... args) throws StrategyException {
                assertSame(connectionPool, args[0]);
                assertEquals(collectionName, args[1]);
                observer = (ISchedulerEntryStorageObserver) args[2];

                return (T) serviceMock;
            }
        });

        activationActionMock = mock(IAction.class);
        IOC.register(Keys.getKeyByName("scheduler service activation action for checkpoint actor"),
                new SingletonStrategy(activationActionMock));

        IOC.register(Keys.getKeyByName("chain_id_from_map_name_and_message"), new IStrategy() {
            @Override
            public <T> T resolve(Object... args) throws StrategyException {
                if ("checkpoint_feedback_chain".equals(args[0])) {
                    return (T) "checkpoint_feedback_chain__0";
                }

                fail("Required some unknown chain: ".concat(String.valueOf(args[0])));
                return null; // UNREACHABLE
            }
        });

        newEntryStrategyMock = mock(IStrategy.class);
        IOC.register(Keys.getKeyByName("new scheduler entry"), newEntryStrategyMock);

        entryMock = new ISchedulerEntry[] {mock(ISchedulerEntry.class), mock(ISchedulerEntry.class), mock(ISchedulerEntry.class)};

        enteringMessageMock = mock(EnteringMessage.class);
        feedbackMessageMock = mock(FeedbackMessage.class);

        messageBusHandlerMock = mock(IMessageBusHandler.class);
        ScopeProvider.getCurrentScope().setValue(MessageBus.getMessageBusKey(), messageBusHandlerMock);

        objectsArgumentCaptor = ArgumentCaptor.forClass(Object.class);
        iObjectArgumentCaptor = ArgumentCaptor.forClass(IObject.class);

        messageProcessorMock = mock(IMessageProcessor.class);
        messageProcessingSequenceMock = mock(IMessageProcessingSequence.class);
        when(messageProcessorMock.getSequence()).thenReturn(messageProcessingSequenceMock);

        upCounterMock = mock(IUpCounter.class);
        IOC.register(Keys.getKeyByName("root upcounter"), new SingletonStrategy(upCounterMock));
    }

    @Test
    public void Should_resolveActivate_StartAndStop_SchedulerService()
            throws Exception {
        IObject args = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                ("{" +
                        "'connectionOptionsDependency':'the connection options'," +
                        "'connectionPoolDependency':'the connection pool'," +
                        "'collectionName':'" + collectionName + "'" +
                        "}").replace('\'','"'));

        CheckpointActor actor = new CheckpointActor(args);

        verify(activationActionMock).execute(serviceMock);

        actor.start(mock(StartStopMessage.class));
        verify(serviceMock).start();

        actor.stop(mock(StartStopMessage.class));
        verify(serviceMock).stop();
    }

    @Test
    public void Should_createNewEntryForNewEnteringMessage()
            throws Exception {
        IObject args = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                ("{" +
                        "'connectionOptionsDependency':'the connection options'," +
                        "'connectionPoolDependency':'the connection pool'," +
                        "'collectionName':'" + collectionName + "'" +
                        "}").replace('\'','"'));

        CheckpointActor actor = new CheckpointActor(args);

        IObject message = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                "{'theUniqueAndVeryImportantField':'42'}".replace('\'','"'));

        when(enteringMessageMock.getCheckpointId()).thenReturn("thisCp");
        when(enteringMessageMock.getCheckpointStatus()).thenReturn(null);
        when(enteringMessageMock.getMessage()).thenReturn(message);
        when(enteringMessageMock.getProcessor()).thenReturn(messageProcessorMock);
        when(enteringMessageMock.getRecoverConfiguration()).thenReturn(mock(IObject.class));
        when(enteringMessageMock.getSchedulingConfiguration()).thenReturn(mock(IObject.class));

        when(newEntryStrategyMock.resolve(objectsArgumentCaptor.capture())).thenReturn(entryMock[0]);
        when(entryMock[0].getId()).thenReturn("entry0");

        actor.enter(enteringMessageMock);

        verify(newEntryStrategyMock).resolve(any(), any());

        assertSame(storageMock, objectsArgumentCaptor.getAllValues().get(1));

        IObject entryArgs = (IObject) objectsArgumentCaptor.getAllValues().get(0);

        assertSame(enteringMessageMock.getRecoverConfiguration(),
                entryArgs.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "recover")));
        assertSame(enteringMessageMock.getSchedulingConfiguration(),
                entryArgs.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "scheduling")));
        assertEquals("checkpoint scheduler action",
                entryArgs.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "action")));
        assertEquals("42",
                ((IObject) entryArgs.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "message")))
                    .getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "theUniqueAndVeryImportantField")));
        assertNotSame(message, entryArgs.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "message")));
        assertSame(messageProcessorMock, entryArgs.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "processor")));

        verify(enteringMessageMock).setCheckpointStatus(iObjectArgumentCaptor.capture());

        IObject newCS = iObjectArgumentCaptor.getValue();

        assertEquals("thisCp", newCS.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "responsibleCheckpointId")));
        assertEquals("entry0", newCS.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "checkpointEntryId")));
    }

    @Test
    public void Should_interruptMessageProcessingWhenEntryIsPresent()
            throws Exception {
        IObject args = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                ("{" +
                        "'connectionOptionsDependency':'the connection options'," +
                        "'connectionPoolDependency':'the connection pool'," +
                        "'collectionName':'" + collectionName + "'" +
                        "}").replace('\'','"'));

        CheckpointActor actor = new CheckpointActor(args);

        IObject status = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                ("{" +
                        "'responsibleCheckpointId':'prevCp'," +
                        "'checkpointEntryId':'prevId'" +
                        "}").replace('\'','"'));

        ISchedulerEntry oldEntry = entryMock[2];
        when(oldEntry.getId()).thenReturn("newIdPresent");
        when(oldEntry.getState()).thenReturn(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                ("{" +
                        "'prevCheckpointId':'prevCp'," +
                        "'prevCheckpointEntryId':'prevId'" +
                        "}").replace('\'','"')));
        observer.onUpdateEntry(oldEntry);

        when(enteringMessageMock.getCheckpointId()).thenReturn("thisCp");
        when(enteringMessageMock.getCheckpointStatus()).thenReturn(status);
        when(enteringMessageMock.getMessage()).thenReturn(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject")));
        when(enteringMessageMock.getProcessor()).thenReturn(messageProcessorMock);
        when(enteringMessageMock.getRecoverConfiguration()).thenReturn(mock(IObject.class));
        when(enteringMessageMock.getSchedulingConfiguration()).thenReturn(mock(IObject.class));

        when(newEntryStrategyMock.resolve(objectsArgumentCaptor.capture())).thenReturn(entryMock[0]);
        when(entryMock[0].getId()).thenReturn("entry0");

        actor.enter(enteringMessageMock);

        verify(messageProcessingSequenceMock).end();
    }

    @Test
    public void Should_sendFeedbackMessage()
            throws Exception {
        IObject args = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                ("{" +
                        "'connectionOptionsDependency':'the connection options'," +
                        "'connectionPoolDependency':'the connection pool'," +
                        "'collectionName':'" + collectionName + "'" +
                        "}").replace('\'','"'));

        CheckpointActor actor = new CheckpointActor(args);

        IObject status = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                ("{" +
                        "'responsibleCheckpointId':'prevCp'," +
                        "'checkpointEntryId':'prevId'" +
                        "}").replace('\'','"'));

        when(enteringMessageMock.getCheckpointId()).thenReturn("thisCp");
        when(enteringMessageMock.getCheckpointStatus()).thenReturn(status);
        when(enteringMessageMock.getMessage()).thenReturn(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject")));
        when(enteringMessageMock.getProcessor()).thenReturn(messageProcessorMock);
        when(enteringMessageMock.getRecoverConfiguration()).thenReturn(mock(IObject.class));
        when(enteringMessageMock.getSchedulingConfiguration()).thenReturn(mock(IObject.class));

        when(newEntryStrategyMock.resolve(objectsArgumentCaptor.capture())).thenReturn(entryMock[0]);
        when(entryMock[0].getId()).thenReturn("entry0");

        actor.enter(enteringMessageMock);

        verify(messageProcessingSequenceMock, never()).end();

        verify(messageBusHandlerMock).handle(iObjectArgumentCaptor.capture(), eq("checkpoint_feedback_chain"), eq(true));

        IObject fbMessage = iObjectArgumentCaptor.getValue();

        assertEquals("thisCp",
                fbMessage.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "responsibleCheckpointId")));
        assertEquals("entry0",
                fbMessage.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "checkpointEntryId")));
        assertEquals("prevCp",
                fbMessage.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "prevCheckpointId")));
        assertEquals("prevId",
                fbMessage.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "prevCheckpointEntryId")));
    }

    @Test
    public void Should_processFeedbackMessages()
            throws Exception {
        long startTime = System.currentTimeMillis();
        IObject args = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                ("{" +
                        "'connectionOptionsDependency':'the connection options'," +
                        "'connectionPoolDependency':'the connection pool'," +
                        "'collectionName':'" + collectionName + "'" +
                        "}").replace('\'','"'));

        IObject entryState = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"));

        CheckpointActor actor = new CheckpointActor(args);

        when(feedbackMessageMock.getPrevCheckpointEntryId()).thenReturn("prevId");
        when(storageMock.getEntry("prevId")).thenReturn(entryMock[0]);
        when(entryMock[0].getState()).thenReturn(entryState);

        actor.feedback(feedbackMessageMock);

        ArgumentCaptor<Long> timeCaptor = ArgumentCaptor.forClass(long.class);

        assertNotNull(entryState.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "completed")));
        verify(entryMock[0]).scheduleNext(timeCaptor.capture());

        assertTrue(timeCaptor.getValue() >= (startTime + 1000));
    }

    @Test
    public void Should_notThrowWhenNoMatchingEntryFoundProcessingFeedbackMessage()
            throws Exception {
        IObject args = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                ("{" +
                        "'connectionOptionsDependency':'the connection options'," +
                        "'connectionPoolDependency':'the connection pool'," +
                        "'collectionName':'" + collectionName + "'" +
                        "}").replace('\'','"'));

        CheckpointActor actor = new CheckpointActor(args);

        when(feedbackMessageMock.getPrevCheckpointEntryId()).thenReturn("prevId");
        when(storageMock.getEntry("prevId")).thenThrow(EntryNotFoundException.class);

        actor.feedback(feedbackMessageMock);
    }

    @Test
    public void Should_ignoreFeedbackMessagesIfTheEntryIsAlreadyCompleted()
            throws Exception {
        IObject args = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                ("{" +
                        "'connectionOptionsDependency':'the connection options'," +
                        "'connectionPoolDependency':'the connection pool'," +
                        "'collectionName':'" + collectionName + "'" +
                        "}").replace('\'','"'));

        IObject entryState = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                ("{'completed':true}").replace('\'','"'));

        CheckpointActor actor = new CheckpointActor(args);

        when(feedbackMessageMock.getPrevCheckpointEntryId()).thenReturn("prevId");
        when(storageMock.getEntry("prevId")).thenReturn(entryMock[0]);
        when(entryMock[0].getState()).thenReturn(entryState);

        actor.feedback(feedbackMessageMock);
        try {
            actor.configure(null);
        } catch (Exception ignore) {}

        verify(entryMock[0]).getState();
        verifyNoMoreInteractions(entryMock[0]);
    }

    @Test
    public void Should_registerUpCounterCallback()
            throws Exception {
        IObject args = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                ("{" +
                        "'connectionOptionsDependency':'the connection options'," +
                        "'connectionPoolDependency':'the connection pool'," +
                        "'collectionName':'" + collectionName + "'" +
                        "}").replace('\'','"'));

        CheckpointActor actor = new CheckpointActor(args);

        ArgumentCaptor<IAction> callbackCaptor = ArgumentCaptor.forClass(IAction.class);
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);

        verify(upCounterMock).onShutdownRequest(keyCaptor.capture(), callbackCaptor.capture());

        callbackCaptor.getValue().execute(null);
        verify(serviceMock).stop();

        doThrow(ServiceStopException.class).when(serviceMock).stop();
        try {
            callbackCaptor.getValue().execute(null);
            fail();
        } catch (ActionExecutionException ignore) { }

        doThrow(IllegalServiceStateException.class).when(serviceMock).stop();
        callbackCaptor.getValue().execute(null);
    }
}
