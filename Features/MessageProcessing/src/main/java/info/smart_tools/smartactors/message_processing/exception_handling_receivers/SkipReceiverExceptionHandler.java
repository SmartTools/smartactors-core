package info.smart_tools.smartactors.message_processing.exception_handling_receivers;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.MessageReceiveException;

/**
 * Receiver that (when placed in exceptional chain) skips the receiver that has thrown an exception.
 */
public class SkipReceiverExceptionHandler extends ExceptionHandlingReceiver {
    /**
     * The constructor.
     *
     * @throws ResolutionException if fails to resolve any dependencies
     */
    public SkipReceiverExceptionHandler()
            throws ResolutionException {
        super();
    }

    @Override
    public void receive(final IMessageProcessor processor)
            throws MessageReceiveException {
        IObject context = processor.getContext();

        try {
            processor.getSequence().goTo(getCauseLevel(context), getCauseStep(context) + 1);
        } catch (ReadValueException | InvalidArgumentException e) {
            throw new MessageReceiveException("Exception occurred while skipping a receiver thrown exception", e);
        }
    }

    @Override
    public void dispose() {
    }
}
