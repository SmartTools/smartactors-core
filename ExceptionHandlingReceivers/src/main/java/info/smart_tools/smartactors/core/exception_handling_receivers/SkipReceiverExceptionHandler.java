package info.smart_tools.smartactors.core.exception_handling_receivers;

import info.smart_tools.smartactors.core.iaction.IAction;
import info.smart_tools.smartactors.core.iaction.exception.ActionExecuteException;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.core.message_processing.exceptions.MessageReceiveException;

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
    public void receive(final IMessageProcessor processor, final IObject arguments, final IAction<Throwable> onEnd)
            throws MessageReceiveException {
        IObject context = processor.getContext();

        try {
            processor.getSequence().goTo(getCauseLevel(context), getCauseStep(context) + 1);

            onEnd.execute(null);
        } catch (ReadValueException | InvalidArgumentException | ActionExecuteException e) {
            throw new MessageReceiveException("Exception occurred while skipping a receiver thrown exception", e);
        }
    }
}
