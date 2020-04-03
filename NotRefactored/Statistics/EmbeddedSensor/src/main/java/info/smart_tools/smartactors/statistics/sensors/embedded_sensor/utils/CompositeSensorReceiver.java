package info.smart_tools.smartactors.statistics.sensors.embedded_sensor.utils;

import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageReceiver;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.AsynchronousOperationException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.MessageReceiveException;

/**
 *
 */
public class CompositeSensorReceiver implements IMessageReceiver {
    private final IMessageReceiver[] receivers;

    /**
     * The constructor.
     *
     * @param receivers    array of the receivers
     */
    public CompositeSensorReceiver(final IMessageReceiver... receivers) {
        this.receivers = receivers;
    }

    @Override
    public void receive(final IMessageProcessor processor) throws MessageReceiveException, AsynchronousOperationException {
        for (IMessageReceiver receiver : receivers) {
            receiver.receive(processor);
        }
    }

    @Override
    public void dispose() {
        for (IMessageReceiver receiver : receivers) {
            try {
                receiver.dispose();
            } catch (Throwable e) {
                e.addSuppressed(e);
                e.printStackTrace();
            }
        }
    }
}
