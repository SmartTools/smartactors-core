package info.smart_tools.smartactors.scheduler.actor.impl.filter;

import info.smart_tools.smartactors.helpers.IOCInitializer.IOCInitializer;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerEntry;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerEntryFilter;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test for {@link SchedulerPreShutdownModeEntryFilter}.
 */
public class SchedulerPreShutdownModeEntryFilterTest extends IOCInitializer {

    @Override
    protected void registry(String... strategyNames) throws Exception {
        registryStrategies("ifieldname strategy", "iobject strategy");
    }

    @Test
    public void Should_permitEntriesWithFlagSetToTrue()
            throws Exception {
        IObject state = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
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
        IObject state = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
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
        IObject state = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
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
        IObject state = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                "{'preShutdownExec':'true'}".replace('\'','"'));
        ISchedulerEntry entryMock = mock(ISchedulerEntry.class);
        when(entryMock.getState()).thenReturn(state);

        ISchedulerEntryFilter filter = new SchedulerPreShutdownModeEntryFilter();

        assertFalse(filter.testAwake(entryMock));
        assertFalse(filter.testExec(entryMock));
        assertFalse(filter.testRestore(state));
    }
}
