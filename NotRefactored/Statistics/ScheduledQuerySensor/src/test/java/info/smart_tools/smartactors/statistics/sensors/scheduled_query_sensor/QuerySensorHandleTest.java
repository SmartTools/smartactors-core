package info.smart_tools.smartactors.statistics.sensors.scheduled_query_sensor;

import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerEntry;
import info.smart_tools.smartactors.statistics.sensors.interfaces.ISensorHandle;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Test for {@link QuerySensorHandle}.
 */
public class QuerySensorHandleTest {
    @Test
    public void Should_cancelEntryOnSensorShutdown()
            throws Exception {
        ISchedulerEntry entry = mock(ISchedulerEntry.class);

        ISensorHandle handle = new QuerySensorHandle(entry);

        handle.shutdown();

        verify(entry).cancel();
    }
}
