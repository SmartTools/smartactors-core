package info.smart_tools.smartactors.scheduler.actor;

import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.interfaces.iaction.IActionNoArgs;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.base.interfaces.ipool.IPool;
import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.interfaces.istrategy.exception.StrategyException;
import info.smart_tools.smartactors.base.isynchronous_service.exceptions.IllegalServiceStateException;
import info.smart_tools.smartactors.base.isynchronous_service.exceptions.ServiceStopException;
import info.smart_tools.smartactors.base.iup_counter.IUpCounter;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.helpers.IOCInitializer.IOCInitializer;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.scheduler.actor.wrappers.*;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerEntry;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerEntryFilter;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerEntryStorage;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerService;
import info.smart_tools.smartactors.task.interfaces.iqueue.IQueue;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

/**
 * Text for {@link SchedulerActor}.
 */
public class SchedulerActorTest extends IOCInitializer {
    private IPool pool;
    private ISchedulerEntryStorage storage;
    private ISchedulerService service;
    private IAction activationAction;
    private IObject args;
    private AddEntryQueryMessage addEntryQueryMessage;
    private SetEntryIdMessage addEntryWithSettingIdMessage;
    private AddEntryQueryListMessage addListEntryQueryMessage;
    private ListEntriesQueryMessage listEntriesQueryMessage;
    private DeleteEntryQueryMessage deleteEntryQueryMessage;
    private IStrategy newEntryStrategy;
    private ISchedulerEntry entryMock;
    private IQueue taskQueueMock;
    private ISchedulerEntryFilter preShutdownModeEntryFilterMock;

    private IUpCounter upCounterMock;

    @Override
    protected void registry(String... strategyNames) throws Exception {
        registryStrategies("ifieldname strategy", "iobject strategy");
    }

    @Override
    protected void registerMocks() throws Exception {
        Object connectionOptions = new Object();
        pool = mock(IPool.class);
        storage = mock(ISchedulerEntryStorage.class);
        service = mock(ISchedulerService.class);
        activationAction = mock(IAction.class);

        IStrategy poolStrategy = mock(IStrategy.class);
        IStrategy serviceStrategy = mock(IStrategy.class);
        IStrategy actionStrategy = mock(IStrategy.class);

        when(service.getEntryStorage()).thenReturn(storage);
        when(poolStrategy.resolve(same(connectionOptions))).thenReturn(pool);
        when(serviceStrategy.resolve(same(pool), eq("the_collection_name")))
                .thenReturn(service)
                .thenThrow(StrategyException.class);
        when(actionStrategy.resolve()).thenReturn(activationAction);

        IOC.register(Keys.getKeyByName("the connection options dependency"), new SingletonStrategy(connectionOptions));
        IOC.register(Keys.getKeyByName("the connection pool dependency"), poolStrategy);
        IOC.register(Keys.getKeyByName("new scheduler service"), serviceStrategy);
        IOC.register(Keys.getKeyByName("scheduler service activation action for scheduler actor"), actionStrategy);

        args = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                ("{" +
                        "'connectionOptionsDependency':'the connection options dependency'," +
                        "'connectionPoolDependency':'the connection pool dependency'," +
                        "'collectionName':'the_collection_name'" +
                        "}").replace('\'','"'));

        addEntryQueryMessage = mock(AddEntryQueryMessage.class);
        when(addEntryQueryMessage.getEntryArguments()).thenReturn(mock(IObject.class));

        addEntryWithSettingIdMessage = mock(SetEntryIdMessage.class);
        when(addEntryWithSettingIdMessage.getEntryArguments()).thenReturn(mock(IObject.class));

        addListEntryQueryMessage = mock(AddEntryQueryListMessage.class);
        when(addListEntryQueryMessage.getEntryArgumentsList()).thenReturn(Collections.singletonList(mock(IObject.class)));

        deleteEntryQueryMessage = mock(DeleteEntryQueryMessage.class);
        when(deleteEntryQueryMessage.getEntryId()).thenReturn("entry-to-delete-id");

        listEntriesQueryMessage = mock(ListEntriesQueryMessage.class);

        entryMock = mock(ISchedulerEntry.class);
        when(entryMock.getState()).thenReturn(mock(IObject.class));
        when(entryMock.getId()).thenReturn("id");

        newEntryStrategy = mock(IStrategy.class);
        when(newEntryStrategy.resolve(any(), any())).thenReturn(entryMock);
        IOC.register(Keys.getKeyByName("new scheduler entry"), newEntryStrategy);

        taskQueueMock = mock(IQueue.class);
        IOC.register(Keys.getKeyByName("task_queue"), new SingletonStrategy(taskQueueMock));

        upCounterMock = mock(IUpCounter.class);
        IOC.register(Keys.getKeyByName("root upcounter"), new SingletonStrategy(upCounterMock));

        preShutdownModeEntryFilterMock = mock(ISchedulerEntryFilter.class);
        IOC.register(Keys.getKeyByName("pre shutdown mode entry filter"), new SingletonStrategy(preShutdownModeEntryFilterMock));
    }

    @Test
    public void Should_activateService()
            throws Exception {
        new SchedulerActor(args);

        verify(activationAction).execute(same(service));
    }

    @Test
    public void Should_startService()
            throws Exception {
        new SchedulerActor(args).start(mock(StartStopMessage.class));

        verify(service).start();
    }

    @Test
    public void Should_stopService()
            throws Exception {
        new SchedulerActor(args).stop(mock(StartStopMessage.class));

        verify(service).stop();
    }

    @Test
    public void Should_createNewEntry()
            throws Exception {
        SchedulerActor actor = new SchedulerActor(args);

        actor.addEntry(addEntryQueryMessage);

        verify(newEntryStrategy).resolve(same(addEntryQueryMessage.getEntryArguments()), same(storage));
    }

    @Test
    public void Should_createEntryAndSetIdToMessage()
            throws Exception {
        SchedulerActor actor = new SchedulerActor(args);

        actor.addEntryWithSettingId(addEntryWithSettingIdMessage);

        verify(newEntryStrategy).resolve(same(addEntryWithSettingIdMessage.getEntryArguments()), same(storage));
        verify(addEntryWithSettingIdMessage).setEntryId(eq("id"));
    }

    @Test
    public void Should_createListOfNewEntry()
            throws Exception {
        SchedulerActor actor = new SchedulerActor(args);

        actor.addEntryList(addListEntryQueryMessage);

        verify(newEntryStrategy).resolve(same(addListEntryQueryMessage.getEntryArgumentsList().get(0)), same(storage));
    }

    @Test
    public void Should_deleteEntry()
            throws Exception {
        when(storage.getEntry("entry-to-delete-id")).thenReturn(entryMock);

        SchedulerActor actor = new SchedulerActor(args);

        actor.deleteEntry(deleteEntryQueryMessage);

        verify(entryMock).cancel();
    }

    @Test
    public void Should_listEntries()
            throws Exception {
        when(storage.listLocalEntries()).thenReturn(Collections.singletonList(entryMock));
        ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);

        SchedulerActor actor = new SchedulerActor(args);

        actor.listEntries(listEntriesQueryMessage);

        verify(listEntriesQueryMessage, times(1)).setEntries(captor.capture());

        assertEquals(1, captor.getValue().size());
        assertSame(entryMock.getState(), captor.getValue().get(0));
    }

    @Test
    public void Should_registerUpCounterShutdownCompletionCallback()
            throws Exception {
        SchedulerActor actor = new SchedulerActor(args);

        ArgumentCaptor<IActionNoArgs> callbackCaptor = ArgumentCaptor.forClass(IActionNoArgs.class);

        verify(upCounterMock).onShutdownComplete(eq(actor.toString()), callbackCaptor.capture());

        callbackCaptor.getValue().execute();
        verify(service).stop();

        doThrow(ServiceStopException.class).when(service).stop();
        try {
            callbackCaptor.getValue().execute();
            fail();
        } catch (ActionExecutionException ignore) { }

        doThrow(IllegalServiceStateException.class).when(service).stop();
        callbackCaptor.getValue().execute();
    }

    @Test
    public void Should_registerUpCounterShutdownRequestCallback()
            throws Exception {
        SchedulerActor actor = new SchedulerActor(args);

        ArgumentCaptor<IAction> callbackCaptor = ArgumentCaptor.forClass(IAction.class);

        verify(upCounterMock).onShutdownRequest(eq(actor.toString()), callbackCaptor.capture());

        callbackCaptor.getValue().execute(null);

        verify(storage).setFilter(same(preShutdownModeEntryFilterMock));
    }
}
