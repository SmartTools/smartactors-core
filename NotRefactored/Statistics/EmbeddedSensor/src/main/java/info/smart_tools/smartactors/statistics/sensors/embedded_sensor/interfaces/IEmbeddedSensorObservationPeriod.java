package info.smart_tools.smartactors.statistics.sensors.embedded_sensor.interfaces;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.statistics.sensors.embedded_sensor.interfaces.exceptions.EmbeddedSensorStrategyException;

/**
 * Interface for a object representing a single period of embedded sensor observation.
 */
public interface IEmbeddedSensorObservationPeriod {
    /**
     * Create the message object containing data of this period.
     *
     * @return the message
     * @throws ResolutionException if error occurs resolving the message object
     * @throws ChangeValueException if error occurs writing message content
     * @throws InvalidArgumentException if error occurs writing message content
     * @throws EmbeddedSensorStrategyException if error occurs querying data from strategy-specific state
     */
    IObject createMessage() throws ResolutionException, ChangeValueException, InvalidArgumentException, EmbeddedSensorStrategyException;

    /**
     * Check if this period is completed at given time.
     *
     * @param time    the time (in milliseconds)
     * @return {@code true} if this period is completed at given time
     */
    boolean isTimeCompleted(long time);

    /**
     * Create object for first of the periods after this one that ends after given time.
     *
     * @param time    the time (in milliseconds)
     * @return new period object
     * @throws EmbeddedSensorStrategyException if error occurs calling methods of sensor strategy
     * @throws ResolutionException if error occurs resolving any dependencies of new object
     */
    IEmbeddedSensorObservationPeriod nextPeriod(long time) throws EmbeddedSensorStrategyException, ResolutionException;

    /**
     * Record data of message processed by given message processor.
     *
     * @param processor    the message processor
     * @param time         the time when the message was received by sensor
     * @return {@code true} if this period is completed
     * @throws EmbeddedSensorStrategyException if error occurs calling methods of sensor strategy
     * @throws InvalidArgumentException if error occurs calling methods of sensor strategy
     */
    boolean recordProcessor(IMessageProcessor processor, long time) throws EmbeddedSensorStrategyException, InvalidArgumentException;
}
