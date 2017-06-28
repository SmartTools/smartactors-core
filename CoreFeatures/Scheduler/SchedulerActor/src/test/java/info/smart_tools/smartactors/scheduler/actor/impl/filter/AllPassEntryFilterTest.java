package info.smart_tools.smartactors.scheduler.actor.impl.filter;

import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerEntry;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Test for {@link AllPassEntryFilter}.
 */
public class AllPassEntryFilterTest {
    @Test
    public void Should_permitAllStateChanges()
            throws Exception {
        assertTrue(
                AllPassEntryFilter.INSTANCE.testAwake(mock(ISchedulerEntry.class)) &&
                AllPassEntryFilter.INSTANCE.testExec(mock(ISchedulerEntry.class)) &&
                AllPassEntryFilter.INSTANCE.testRestore(mock(IObject.class))
        );
    }
}
