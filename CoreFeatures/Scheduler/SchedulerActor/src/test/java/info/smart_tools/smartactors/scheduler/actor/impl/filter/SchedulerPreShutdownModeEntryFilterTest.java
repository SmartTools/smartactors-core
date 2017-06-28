package info.smart_tools.smartactors.scheduler.actor.impl.filter;

import info.smart_tools.smartactors.helpers.plugins_loading_test_base.PluginsLoadingTestBase;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject_plugins.dsobject_plugin.PluginDSObject;
import info.smart_tools.smartactors.iobject_plugins.ifieldname_plugin.IFieldNamePlugin;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import info.smart_tools.smartactors.ioc_plugins.ioc_keys_plugin.PluginIOCKeys;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerEntry;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerEntryFilter;
import info.smart_tools.smartactors.scope_plugins.scope_provider_plugin.PluginScopeProvider;
import info.smart_tools.smartactors.scope_plugins.scoped_ioc_plugin.ScopedIOCPlugin;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test for {@link SchedulerPreShutdownModeEntryFilter}.
 */
public class SchedulerPreShutdownModeEntryFilterTest extends PluginsLoadingTestBase {
    @Override
    protected void loadPlugins() throws Exception {
        load(ScopedIOCPlugin.class);
        load(PluginScopeProvider.class);
        load(PluginIOCKeys.class);
        load(PluginDSObject.class);
        load(IFieldNamePlugin.class);
    }

    @Test
    public void Should_permitEntriesWithFlagSetToTrue()
            throws Exception {
        IObject state = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()),
                "{'preShutdownExec':true}".replace('\'','"'));
        ISchedulerEntry entryMock = mock(ISchedulerEntry.class);
        when(entryMock.getState()).thenReturn(state);

        ISchedulerEntryFilter filter = new SchedulerPreShutdownModeEntryFilter();

        assertTrue(filter.testAwake(entryMock));
        assertTrue(filter.testExec(entryMock));
        assertTrue(filter.testRestore(state));
    }

    @Test
    public void Should_notPermitEntriesWithFlagSetToFalse()
            throws Exception {
        IObject state = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()),
                "{'preShutdownExec':false}".replace('\'','"'));
        ISchedulerEntry entryMock = mock(ISchedulerEntry.class);
        when(entryMock.getState()).thenReturn(state);

        ISchedulerEntryFilter filter = new SchedulerPreShutdownModeEntryFilter();

        assertFalse(filter.testAwake(entryMock));
        assertFalse(filter.testExec(entryMock));
        assertFalse(filter.testRestore(state));
    }

    @Test
    public void Should_notPermitEntriesWithFlagUnset()
            throws Exception {
        IObject state = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()),
                "{}".replace('\'','"'));
        ISchedulerEntry entryMock = mock(ISchedulerEntry.class);
        when(entryMock.getState()).thenReturn(state);

        ISchedulerEntryFilter filter = new SchedulerPreShutdownModeEntryFilter();

        assertFalse(filter.testAwake(entryMock));
        assertFalse(filter.testExec(entryMock));
        assertFalse(filter.testRestore(state));
    }

    @Test
    public void Should_notPermitEntriesWithFlagSetToInvalidValue()
            throws Exception {
        IObject state = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()),
                "{'preShutdownExec':'true'}".replace('\'','"'));
        ISchedulerEntry entryMock = mock(ISchedulerEntry.class);
        when(entryMock.getState()).thenReturn(state);

        ISchedulerEntryFilter filter = new SchedulerPreShutdownModeEntryFilter();

        assertFalse(filter.testAwake(entryMock));
        assertFalse(filter.testExec(entryMock));
        assertFalse(filter.testRestore(state));
    }
}
