package info.smart_tools.smartactors.statistics.sensors.scheduled_query_sensor;

import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerEntry;
import info.smart_tools.smartactors.scheduler.interfaces.exceptions.EntryScheduleException;
import info.smart_tools.smartactors.scheduler.interfaces.exceptions.EntryStorageAccessException;
import info.smart_tools.smartactors.statistics.sensors.interfaces.ISensorHandle;
import info.smart_tools.smartactors.statistics.sensors.interfaces.exceptions.SensorShutdownException;

/**
 *
 */
public class QuerySensorHandle implements ISensorHandle {
    private final ISchedulerEntry entry;

    /**
     * The constructor.
     *
     * @param entry    the scheduler entry that is associated with the sensor
     */
    public QuerySensorHandle(final ISchedulerEntry entry) {
        this.entry = entry;
    }

    @Override
    public void shutdown() throws SensorShutdownException {
        try {
            entry.cancel();
        } catch (EntryStorageAccessException | EntryScheduleException e) {
            throw new SensorShutdownException("Could not cancel sensor entry.", e);
        }
    }
}
