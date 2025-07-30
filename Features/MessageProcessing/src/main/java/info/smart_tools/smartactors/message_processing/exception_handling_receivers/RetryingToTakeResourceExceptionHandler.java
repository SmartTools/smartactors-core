package info.smart_tools.smartactors.message_processing.exception_handling_receivers;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.base.interfaces.iresource_source.exceptions.OutOfResourceException;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.AsynchronousOperationException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.MessageReceiveException;

/**
 * This handler should be set in chain handling {@link OutOfResourceException}. Waits for the resource to become available
 * and restarts execution of handler thrown exception.
 */
public class RetryingToTakeResourceExceptionHandler extends ExceptionHandlingReceiver {
    /**
     * The constructor.
     *
     * @throws ResolutionException if fails to resolve any dependencies
     */
    public RetryingToTakeResourceExceptionHandler()
            throws ResolutionException {
        super();
    }

    @Override
    public void receive(final IMessageProcessor processor)
            throws MessageReceiveException {
        try {
            IObject context = processor.getContext();
            OutOfResourceException exception = (OutOfResourceException) getException(context);

            processor.getSequence().goTo(getCauseLevel(context), getCauseStep(context));

            processor.pauseProcess();

            exception.getSource().onAvailable(() -> {
                try {
                    processor.continueProcess(null);
                } catch (AsynchronousOperationException e) {
                    throw new ActionExecutionException(e);
                }
            });
        } catch (ReadValueException | InvalidArgumentException | AsynchronousOperationException e) {
            throw new MessageReceiveException(e);
        }
    }

    @Override
    public void dispose() {
    }
}
