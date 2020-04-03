package info.smart_tools.smartactors.statistics.sensors.embedded_sensor.interfaces.exceptions;

/**
 * Exception thrown by {@link info.smart_tools.smartactors.statistics.sensors.embedded_sensor.interfaces.IEmbeddedSensorStrategy}.
 */
public class EmbeddedSensorStrategyException extends Exception {
    /**
     * The constructor.
     *
     * @param message       the message
     * @param cause         the cause
     */
    public EmbeddedSensorStrategyException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * The constructor.
     *
     * @param message       the message
     */
    public EmbeddedSensorStrategyException(final String message) {
        super(message);
    }

    /**
     * The constructor.
     *
     * @param cause         the cause
     */
    public EmbeddedSensorStrategyException(final Throwable cause) {
        super(cause);
    }
}
