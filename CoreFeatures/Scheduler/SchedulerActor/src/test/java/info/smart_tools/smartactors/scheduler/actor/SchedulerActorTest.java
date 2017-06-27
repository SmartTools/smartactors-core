package info.smart_tools.smartactors.scheduler.actor;

import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.interfaces.ipool.IPool;
import info.smart_tools.smartactors.base.interfaces.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.base.interfaces.iresolve_dependency_strategy.exception.ResolveDependencyStrategyException;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.field_plugins.ifield_plugin.IFieldPlugin;
import info.smart_tools.smartactors.helpers.plugins_loading_test_base.PluginsLoadingTestBase;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject_plugins.dsobject_plugin.PluginDSObject;
import info.smart_tools.smartactors.iobject_plugins.ifieldname_plugin.IFieldNamePlugin;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import info.smart_tools.smartactors.ioc_plugins.ioc_keys_plugin.PluginIOCKeys;
import info.smart_tools.smartactors.scheduler.actor.wrappers.*;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerEntry;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerEntryStorage;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerService;
import info.smart_tools.smartactors.scope_plugins.scope_provider_plugin.PluginScopeProvider;
import info.smart_tools.smartactors.scope_plugins.scoped_ioc_plugin.ScopedIOCPlugin;
import info.smart_tools.smartactors.task.interfaces.iqueue.IQueue;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

/**
 * Text for {@link SchedulerActor}.
 */
public class SchedulerActorTest extends PluginsLoadingTestBase {
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
    private IResolveDependencyStrategy newEntryStrategy;
    private ISchedulerEntry entryMock;
    private IQueue taskQueueMock;

    @Override
    protected void loadPlugins() throws Exception {
        load(ScopedIOCPlugin.class);
        load(PluginScopeProvider.class);
        load(PluginIOCKeys.class);
        load(PluginDSObject.class);
        load(IFieldNamePlugin.class);
        load(IFieldPlugin.class);
    }

    @Override
    protected void registerMocks() throws Exception {
        Object connectionOptions = new Object();
        pool = mock(IPool.class);
        storage = mock(ISchedulerEntryStorage.class);
        service = mock(ISchedulerService.class);
        activationAction = mock(IAction.class);

        IResolveDependencyStrategy poolStrategy = mock(IResolveDependencyStrategy.class);
        IResolveDependencyStrategy serviceStrategy = mock(IResolveDependencyStrategy.class);
        IResolveDependencyStrategy actionStrategy = mock(IResolveDependencyStrategy.class);

        when(service.getEntryStorage()).thenReturn(storage);
        when(poolStrategy.resolve(same(connectionOptions))).thenReturn(pool);
        when(serviceStrategy.resolve(same(pool), eq("the_collection_name")))
                .thenReturn(service)
                .thenThrow(ResolveDependencyStrategyException.class);
        when(actionStrategy.resolve()).thenReturn(activationAction);

        IOC.register(Keys.getOrAdd("the connection options dependency"), new SingletonStrategy(connectionOptions));
        IOC.register(Keys.getOrAdd("the connection pool dependency"), poolStrategy);
        IOC.register(Keys.getOrAdd("new scheduler service"), serviceStrategy);
        IOC.register(Keys.getOrAdd("scheduler service activation action for scheduler actor"), actionStrategy);

        args = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()),
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

        newEntryStrategy = mock(IResolveDependencyStrategy.class);
        when(newEntryStrategy.resolve(any(), any())).thenReturn(entryMock);
        IOC.register(Keys.getOrAdd("new scheduler entry"), newEntryStrategy);

        taskQueueMock = mock(IQueue.class);
        IOC.register(Keys.getOrAdd("task_queue"), new SingletonStrategy(taskQueueMock));
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
}