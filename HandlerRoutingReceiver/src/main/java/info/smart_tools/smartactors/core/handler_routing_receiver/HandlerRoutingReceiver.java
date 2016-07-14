package info.smart_tools.smartactors.core.handler_routing_receiver;

import info.smart_tools.smartactors.core.ifield.IField;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.core.message_processing.IMessageReceiver;
import info.smart_tools.smartactors.core.message_processing.exceptions.AsynchronousOperationException;
import info.smart_tools.smartactors.core.message_processing.exceptions.MessageReceiveException;

import java.util.Map;

/**
 * {@link IMessageReceiver} that invokes one of nested receivers depending on value of a field of arguments object passed.
 */
public class HandlerRoutingReceiver implements IMessageReceiver {
    private final Map<Object, IMessageReceiver> handlerReceiversMap;
    private final IField handlerField;

    /**
     * The constructor.
     *
     * @param handlerReceiversMap    map from a handler identifier to receiver invoking that handler
     * @throws InvalidArgumentException if handlerReceiversMap is {@code null}.
     * @throws ResolutionException if resolution of dependencies fails.
     */
    public HandlerRoutingReceiver(final Map<Object, IMessageReceiver> handlerReceiversMap)
            throws InvalidArgumentException, ResolutionException {
        if (null == handlerReceiversMap) {
            throw new InvalidArgumentException("Map should not be null.");
        }

        this.handlerReceiversMap = handlerReceiversMap;
        this.handlerField = IOC.resolve(IOC.resolve(IOC.getKeyForKeyStorage(), IField.class.getCanonicalName()), "handler");
    }

    @Override
    public void receive(final IMessageProcessor processor)
            throws MessageReceiveException, AsynchronousOperationException {
        try {
            Object handlerId = handlerField.in(processor.getSequence().getCurrentReceiverArguments());
            IMessageReceiver handlerReceiver = handlerReceiversMap.get(handlerId);

            if (null == handlerReceiver) {
                throw new MessageReceiveException("Handler not found.");
            } else {
                handlerReceiver.receive(processor);
            }
        } catch (ReadValueException | InvalidArgumentException e) {
            throw new MessageReceiveException("Error reading handler name.");
        }
    }
}
