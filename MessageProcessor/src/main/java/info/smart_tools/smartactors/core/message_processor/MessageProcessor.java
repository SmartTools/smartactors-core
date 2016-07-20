package info.smart_tools.smartactors.core.message_processor;

import info.smart_tools.smartactors.core.iaction.IAction;
import info.smart_tools.smartactors.core.iaction.exception.ActionExecuteException;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.iqueue.IQueue;
import info.smart_tools.smartactors.core.itask.ITask;
import info.smart_tools.smartactors.core.itask.exception.TaskExecutionException;
import info.smart_tools.smartactors.core.message_processing.IMessageProcessingSequence;
import info.smart_tools.smartactors.core.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.core.message_processing.IMessageReceiver;

/**
 * Task that performs on a message actions defined by a message processing sequence.
 *
 * @see IMessageProcessingSequence
 * @see ITask
 */
public class MessageProcessor implements ITask, IMessageProcessor {
    private IObject context;
    private IObject message;
    private IObject response;

    private final IQueue<ITask> taskQueue;
    private final IMessageProcessingSequence messageProcessingSequence;

    private final ReceiverCallback receiverCallback;

    /**
     * {@link IAction} that will be passed to {@link IMessageReceiver}'s.
     */
    private class ReceiverCallback implements IAction<Throwable> {
        @Override
        public void execute(final Throwable exception)
                throws ActionExecuteException, InvalidArgumentException {
            if (null != exception) {
                try {
                    handleCompletedExceptionally(exception);
                } catch (Exception e) {
                    // TODO: Exception rethrown by receiver may be caught successful by MessageProcessor#execute()
                    complete();
                    throw new ActionExecuteException("Exception occurred while handling exception occurred in message receiver.", e);
                }
            } else {
                if (messageProcessingSequence.next()) {
                    MessageProcessor.this.enqueue();
                } else {
                    MessageProcessor.this.complete();
                }
            }
        }
    }

    /**
     * The constructor.
     *
     * @param taskQueue                    the queue to be executed from
     * @param messageProcessingSequence    a {@link IMessageProcessingSequence} to use
     * @throws InvalidArgumentException if taskQueue is {@code null}
     * @throws InvalidArgumentException if messageProcessingSequence is {@code null}
     * @throws ResolutionException if failed to resolve any dependency
     */
    public MessageProcessor(final IQueue<ITask> taskQueue, final IMessageProcessingSequence messageProcessingSequence)
            throws InvalidArgumentException, ResolutionException {
        if (null == taskQueue) {
            throw new InvalidArgumentException("Task queue should not be null.");
        }

        if (null == messageProcessingSequence) {
            throw new InvalidArgumentException("Message processing sequence should not be null.");
        }

        this.taskQueue = taskQueue;
        this.messageProcessingSequence = messageProcessingSequence;

        this.receiverCallback = new ReceiverCallback();
    }

    /**
     * Process given message in given context.
     *
     * @param theMessage the message to process
     * @param theContext the context to process message in
     * @throws InvalidArgumentException if theMessage is {@code null}
     * @throws InvalidArgumentException if theContext is {@code null}
     * @throws ResolutionException if fails to resolve any dependency
     */
    @Override
    public void process(final IObject theMessage, final IObject theContext)
            throws InvalidArgumentException, ResolutionException {
        // TODO: Ensure that there is no process in progress
        this.message = theMessage;
        this.context = theContext;
        this.response = IOC.resolve(IOC.resolve(IOC.getKeyForKeyStorage(), IObject.class));
        messageProcessingSequence.reset();
        // TODO: Save messageProcessingSequence in context (?).
        enqueue();
    }

    @Override
    public IObject getContext() {
        return context;
    }

    @Override
    public IObject getResponse() {
        return response;
    }

    @Override
    public  IObject getMessage() {
        return message;
    }

    @Override
    public IMessageProcessingSequence getSequence() {
        return messageProcessingSequence;
    }

    @Override
    public void execute() throws TaskExecutionException {
        try {
            messageProcessingSequence.getCurrentReceiver().receive(
                    this, messageProcessingSequence.getCurrentReceiverArguments(), receiverCallback);
        } catch (Throwable e) {
            try {
                handleCompletedExceptionally(e);
            } catch (final Exception e1) {
                complete();
                throw new TaskExecutionException("Exception occurred while handling exception occurred in message receiver.", e1);
            }
        }
    }

    private void handleCompletedExceptionally(final Throwable exception)
            throws Exception {
        messageProcessingSequence.catchException(exception, context);
        enqueue();
    }

    private void enqueue() {
        try {
            taskQueue.put(this);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void complete() {
        // TODO: Return message, context, response and {@code this} to the pool
    }
}
