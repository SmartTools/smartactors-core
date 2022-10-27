package info.smart_tools.smartactors.statistics.sensors.embedded_sensor.interfaces;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.statistics.sensors.embedded_sensor.interfaces.exceptions.EmbeddedSensorStrategyException;

import java.util.Collection;

/**
 * @param <TPeriodState> type of a object representing state of observation for a single period
 */
public interface IEmbeddedSensorStrategy<TPeriodState> {
    /**
     * Start new observation period.
     *
     * @return the period observation state
     * @throws EmbeddedSensorStrategyException if any error occurs
     */
    TPeriodState initPeriod() throws EmbeddedSensorStrategyException;

    /**
     * Record information about a message passing the sensor.
     *
     * @param period       the current period observation state
     * @param processor    the message processor processing the message
     * @param time         the time (in milliseconds, as returned by {@link System#currentTimeMillis()}) when the message was received
     * @throws EmbeddedSensorStrategyException if any error occurs
     * @throws InvalidArgumentException if any of arguments has invalid value
     */
    void updatePeriod(TPeriodState period, IMessageProcessor processor, long time)
            throws EmbeddedSensorStrategyException, InvalidArgumentException;

    /**
     * Get all the measurements from given period state.
     *
     * @param period    the period observation state
     * @return collection of measurements
     * @throws EmbeddedSensorStrategyException if any error occurs
     * @throws InvalidArgumentException if {@code period} is not valid period created by {@link #initPeriod()} method of this strategy
     */
    Collection<? extends Number> extractPeriod(TPeriodState period) throws EmbeddedSensorStrategyException, InvalidArgumentException;
}
