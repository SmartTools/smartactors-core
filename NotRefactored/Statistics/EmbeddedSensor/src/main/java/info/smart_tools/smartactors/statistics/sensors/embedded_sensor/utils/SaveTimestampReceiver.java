package info.smart_tools.smartactors.statistics.sensors.embedded_sensor.utils;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageReceiver;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.AsynchronousOperationException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.MessageReceiveException;
import info.smart_tools.smartactors.timer.interfaces.itimer.ITime;

/**
 * Receiver that saves current system time in field of message context.
 */
public class SaveTimestampReceiver implements IMessageReceiver {
    private final ITime systemTime;
    private final IFieldName timeContextField;

    /**
     * The constructor.
     *
     * @param fieldName    name of the context field where to store current time
     * @throws ResolutionException if error occurs resolving any dependencies
     */
    public SaveTimestampReceiver(final IFieldName fieldName)
            throws ResolutionException {
        this.timeContextField = fieldName;
        this.systemTime = IOC.resolve(Keys.getKeyByName("time"));
    }

    @Override
    public void receive(final IMessageProcessor processor) throws MessageReceiveException, AsynchronousOperationException {
        try {
            processor.getContext().setValue(timeContextField, systemTime.currentTimeMillis());
        } catch (ChangeValueException | InvalidArgumentException e) {
            throw new MessageReceiveException("Could not save current time in message context.", e);
        }
    }

    @Override
    public void dispose() {
    }
}
