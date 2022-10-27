package info.smart_tools.smartactors.statistics.sensors.interfaces.exceptions;

import info.smart_tools.smartactors.statistics.sensors.interfaces.ISensorHandle;

/**
 * Exception thrown by {@link ISensorHandle#shutdown()} when error occurs shutting down the sensor.
 */
public class SensorShutdownException extends Exception {
    /**
     * The constructor.
     *
     * @param message       the message
     * @param cause         the cause
     */
    public SensorShutdownException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * The constructor.
     *
     * @param message       the message
     */
    public SensorShutdownException(final String message) {
        super(message);
    }

    /**
     * The constructor.
     *
     * @param cause         the cause
     */
    public SensorShutdownException(final Throwable cause) {
        super(cause);
    }
}
