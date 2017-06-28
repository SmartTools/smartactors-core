package info.smart_tools.smartactors.scheduler.actor.impl;

import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.message_bus.interfaces.imessage_bus_handler.IMessageBusHandler;
import info.smart_tools.smartactors.message_bus.message_bus.MessageBus;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerAction;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerEntry;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerEntryFilter;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulingStrategy;
import info.smart_tools.smartactors.field_plugins.ifield_plugin.IFieldPlugin;
import info.smart_tools.smartactors.helpers.plugins_loading_test_base.PluginsLoadingTestBase;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject_plugins.dsobject_plugin.PluginDSObject;
import info.smart_tools.smartactors.iobject_plugins.ifieldname_plugin.IFieldNamePlugin;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import info.smart_tools.smartactors.ioc_plugins.ioc_keys_plugin.PluginIOCKeys;
import info.smart_tools.smartactors.scope.scope_provider.ScopeProvider;
import info.smart_tools.smartactors.scope_plugins.scope_provider_plugin.PluginScopeProvider;
import info.smart_tools.smartactors.scope_plugins.scoped_ioc_plugin.ScopedIOCPlugin;
import info.smart_tools.smartactors.task.interfaces.itask.ITask;
import info.smart_tools.smartactors.timer.interfaces.itimer.ITimer;
import info.smart_tools.smartactors.timer.interfaces.itimer.ITimerTask;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

/**
 * Test for {@link EntryImpl}.
 */
public class EntryImplTest extends PluginsLoadingTestBase {
    private ISchedulingStrategy strategy;
    private EntryStorage storage;
    private ITimer timer;
    private ITimerTask timerTask;
    private ITimerTask timerTask2;
    private ISchedulerAction action;
    private ISchedulerEntryFilter filter;

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
        strategy = mock(ISchedulingStrategy.class);
        storage = mock(EntryStorage.class);
        timer = mock(ITimer.class);
        timerTask = mock(ITimerTask.class);
        timerTask2 = mock(ITimerTask.class);
        action = new DefaultSchedulerAction();
        filter = mock(ISchedulerEntryFilter.class);

        when(filter.testExec(any())).thenReturn(true);
        when(storage.getFilter()).thenReturn(filter);

        when(storage.getTimer()).thenReturn(timer);
        when(timer.schedule(any(), anyLong())).thenReturn(timerTask).thenThrow(AssertionError.class);

        IOC.register(Keys.getOrAdd("default scheduler action"), new SingletonStrategy(action));
    }

    @Test
    public void Should_getIdFromState()
            throws Exception {
        IObject state = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()),
                "{'entryId':'trust-methis-isa-guid'}".replace('\'','"'));

        ISchedulerEntry entry = new EntryImpl(state, strategy, storage, action, false);

        assertEquals("trust-methis-isa-guid", entry.getId());
        assertEquals(Long.MAX_VALUE, entry.getLastTime());
        assertSame(state, entry.getState());
    }

    @Test
    public void Should_saveItself()
            throws Exception {
        IObject state = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()),
                "{'entryId':'trust-methis-isa-guid'}".replace('\'','"'));

        ISchedulerEntry entry = new EntryImpl(state, strategy, storage, action, false);

        entry.save();

        verify(storage).save(same(entry));
    }

    @Test
    public void Should_rescheduleItself()
            throws Exception {
        IObject state = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()),
                "{'entryId':'trust-methis-isa-guid'}".replace('\'','"'));

        ISchedulerEntry entry = new EntryImpl(state, strategy, storage, action, false);

        entry.scheduleNext(100500);

        assertEquals(100500, entry.getLastTime());
        verify(timer).schedule(any(), eq(100500L));

        entry.scheduleNext(200500);

        assertEquals(200500, entry.getLastTime());
        verify(timerTask).reschedule(eq(200500L));
    }

    @Test
    public void Should_cancelAndDeleteItselfOnlyOnce()
            throws Exception {
        IObject state = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()),
                "{'entryId':'trust-methis-isa-guid'}".replace('\'','"'));

        ISchedulerEntry entry = new EntryImpl(state, strategy, storage, action, false);

        entry.scheduleNext(300500);

        entry.cancel();
        entry.cancel();

        verify(timerTask, times(1)).cancel();
        verify(storage, times(1)).delete(same(entry));
    }

    @Test
    public void Should_sendMessageWhenTaskExecuted()
            throws Exception {
        IObject state = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()),
                "{'entryId':'trust-methis-isa-guid', 'message':{'this is':'the message'}}".replace('\'','"'));

        ISchedulerEntry entry = new EntryImpl(state, strategy, storage, action, false);

        entry.scheduleNext(0);

        ArgumentCaptor<ITask> taskArgumentCaptor = ArgumentCaptor.forClass(ITask.class);
        ArgumentCaptor<IObject> iObjectArgumentCaptor = ArgumentCaptor.forClass(IObject.class);

        verify(timer).schedule(taskArgumentCaptor.capture(), eq(0L));

        IMessageBusHandler messageBusHandler = mock(IMessageBusHandler.class);

        ScopeProvider.getCurrentScope().setValue(MessageBus.getMessageBusKey(), messageBusHandler);

        taskArgumentCaptor.getValue().execute();

        verify(messageBusHandler).handle(iObjectArgumentCaptor.capture());
        verify(strategy).postProcess(same(entry));

        assertEquals("{'this is':'the message'}".replace('\'','"'), iObjectArgumentCaptor.getValue().serialize());
    }

    @Test
    public void Should_restoreSavedEntry()
            throws Exception {
        IOC.register(Keys.getOrAdd("neverschedule strategy"), new SingletonStrategy(strategy));

        IObject state = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()),
                "{'entryId':'trust-methis-isa-guid','strategy':'neverschedule strategy'}".replace('\'','"'));

        ISchedulerEntry entry = EntryImpl.restoreEntry(state, storage);

        assertEquals("trust-methis-isa-guid", entry.getId());
        verify(strategy).restore(same(entry));
    }

    @Test
    public void Should_createNewEntry()
            throws Exception {
        IOC.register(Keys.getOrAdd("neverschedule strategy"), new SingletonStrategy(strategy));

        IObject args = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()),
                "{'strategy':'neverschedule strategy','message':{}}".replace('\'','"'));

        ISchedulerEntry entry = EntryImpl.newEntry(args, storage);

        assertNotNull(entry.getId());
        verify(strategy).init(same(entry), same(args));
    }

    @Test
    public void Should_createNewEntryWithNestedSchedulingConfigurationObject()
            throws Exception {
        IOC.register(Keys.getOrAdd("neverschedule strategy"), new SingletonStrategy(strategy));

        IObject args = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()),
                "{'scheduling':{'strategy':'neverschedule strategy'},'message':{}}".replace('\'','"'));

        ISchedulerEntry entry = EntryImpl.newEntry(args, storage);

        assertNotNull(entry.getId());
        verify(strategy).init(
                same(entry),
                same((IObject) args.getValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "scheduling"))));
    }

    @Test
    public void Should_suspendEntryIdempotent()
            throws Exception {
        IOC.register(Keys.getOrAdd("neverschedule strategy"), new SingletonStrategy(strategy));

        IObject state = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()),
                "{'entryId':'trust-methis-isa-guid','strategy':'neverschedule strategy'}".replace('\'','"'));

        ISchedulerEntry entry = EntryImpl.restoreEntry(state, storage);

        entry.scheduleNext(100);

        reset(storage, timerTask);

        entry.suspend();

        verify(storage).notifyInactive(entry, false);
        verify(timerTask).cancel();

        entry.suspend();

        verifyNoMoreInteractions(storage, timerTask);
    }

    @Test
    public void Should_awakeEntry()
            throws Exception {
        when(timer.schedule(any(), anyLong()))
                .thenReturn(timerTask)
                .thenReturn(timerTask2)
                .thenThrow(AssertionError.class);
        IOC.register(Keys.getOrAdd("neverschedule strategy"), new SingletonStrategy(strategy));

        IObject state = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()),
                "{'entryId':'trust-methis-isa-guid','strategy':'neverschedule strategy'}".replace('\'','"'));

        ISchedulerEntry entry = EntryImpl.restoreEntry(state, storage);

        entry.scheduleNext(100);

        entry.suspend();

        reset(storage, timer);
        when(storage.getTimer()).thenReturn(timer);

        entry.awake();

        verify(storage).notifyActive(entry);
        verify(storage, atLeast(1)).getTimer();
        verify(timer).schedule(any(), eq(100L));

        entry.suspend();

        verifyNoMoreInteractions(storage, timer);
    }
}
