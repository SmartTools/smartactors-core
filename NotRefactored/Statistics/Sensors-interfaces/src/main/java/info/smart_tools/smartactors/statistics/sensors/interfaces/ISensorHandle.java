package info.smart_tools.smartactors.statistics.sensors.interfaces;

import info.smart_tools.smartactors.statistics.sensors.interfaces.exceptions.SensorShutdownException;

/**
 * Interface that provides methods to control state of a sensor.
 */
public interface ISensorHandle {
    /**
     * Shutdown the sensor. After call to this method the sensor should not send any data. However there may remain some messages from the
     * sensor being processed.
     *
     * @throws SensorShutdownException if any error occurs
     */
    void shutdown() throws SensorShutdownException;
}
