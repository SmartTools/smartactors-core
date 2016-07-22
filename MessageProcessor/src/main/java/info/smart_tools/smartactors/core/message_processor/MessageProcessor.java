package info.smart_tools.smartactors.core.message_processor;

import info.smart_tools.smartactors.core.ifield_name.IFieldName;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.iqueue.IQueue;
import info.smart_tools.smartactors.core.itask.ITask;
import info.smart_tools.smartactors.core.itask.exception.TaskExecutionException;
import info.smart_tools.smartactors.core.message_processing.IMessageProcessingSequence;
import info.smart_tools.smartactors.core.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.core.message_processing.exceptions.AsynchronousOperationException;

/**
 * Task that performs on a message actions defined by a message processing sequence.
 *
 * @see IMessageProcessingSequence
 * @see ITask
 */
public class MessageProcessor implements ITask, IMessageProcessor {
    private IObject config;
    private IObject context;
    private IObject message;
    private IObject response;
    private IObject environment;

    private IFieldName configFieldName;
    private IFieldName messageFieldName;
    private IFieldName contextFieldName;
    private IFieldName responseFieldName;
    private IFieldName sequenceFieldName;
    private IFieldName argumentsFieldName;

    /**
     * True if processing was interrupted (using {@link #pauseProcess()}) during execution of last receiver.
     */
    private boolean interrupted;

    /**
     * Depth of asynchronous operations. Any asynchronous operation (started by {@link #pauseProcess()}) may start another
     * before it is completed.
     *
     * Example is a actor that wants to interrupt message processing (execution of actor is asynchronous operation itself
     * as may require awaiting for a actor to finish previous operations).
     */
    // TODO: Use atomic if any more dangerous situation than described one will appear (and race condition will be possible)
    private int asyncOpDepth;

    /**
     * Exception occurred in a asynchronous operation. If more than one asynchronous operation is performed only the last
     * one exception will be processed using chain-level exception handling. Exceptions from another operations will be
     * suppressed.
     *
     * @see IMessageProcessingSequence#catchException
     */
    private Throwable asyncException;

    private final IQueue<ITask> taskQueue;
    private final IMessageProcessingSequence messageProcessingSequence;

    /**
     * The constructor.
     *
     * @param taskQueue                    the queue to be executed from
     * @param messageProcessingSequence    a {@link IMessageProcessingSequence} to use
     * @param config                       the global configuration object to use
     * @throws InvalidArgumentException if taskQueue is {@code null}
     * @throws InvalidArgumentException if messageProcessingSequence is {@code null}
     * @throws ResolutionException if failed to resolve any dependency
     */
    public MessageProcessor(final IQueue<ITask> taskQueue, final IMessageProcessingSequence messageProcessingSequence, final IObject config)
            throws InvalidArgumentException, ResolutionException {
        if (null == taskQueue) {
            throw new InvalidArgumentException("Task queue should not be null.");
        }

        if (null == messageProcessingSequence) {
            throw new InvalidArgumentException("Message processing sequence should not be null.");
        }

        if (null == config) {
            throw new InvalidArgumentException("Configuration object should not be null.");
        }

        this.taskQueue = taskQueue;
        this.messageProcessingSequence = messageProcessingSequence;
        this.config = config;

        this.interrupted = false;
        this.asyncOpDepth = 0;

        this.environment = IOC.resolve(IOC.resolve(IOC.getKeyForKeyStorage(), IObject.class.getCanonicalName()));

        configFieldName = IOC.resolve(IOC.resolve(IOC.getKeyForKeyStorage(), IFieldName.class.getCanonicalName()), "config");
        messageFieldName = IOC.resolve(IOC.resolve(IOC.getKeyForKeyStorage(), IFieldName.class.getCanonicalName()), "message");
        contextFieldName = IOC.resolve(IOC.resolve(IOC.getKeyForKeyStorage(), IFieldName.class.getCanonicalName()), "context");
        responseFieldName = IOC.resolve(IOC.resolve(IOC.getKeyForKeyStorage(), IFieldName.class.getCanonicalName()), "response");
        sequenceFieldName = IOC.resolve(IOC.resolve(IOC.getKeyForKeyStorage(), IFieldName.class.getCanonicalName()), "sequence");
        argumentsFieldName = IOC.resolve(IOC.resolve(IOC.getKeyForKeyStorage(), IFieldName.class.getCanonicalName()), "arguments");
    }

    @Override
    public void process(final IObject theMessage, final IObject theContext)
            throws InvalidArgumentException, ResolutionException, ChangeValueException {
        // TODO: Ensure that there is no process in progress
        this.message = theMessage;
        this.context = theContext;
        this.response = IOC.resolve(IOC.resolve(IOC.getKeyForKeyStorage(), IObject.class.getCanonicalName()));

        environment.setValue(configFieldName, config);
        environment.setValue(sequenceFieldName, messageProcessingSequence);
        environment.setValue(responseFieldName, response);
        environment.setValue(messageFieldName, theMessage);
        environment.setValue(contextFieldName, theContext);

        messageProcessingSequence.reset();
        enqueue();
    }

    @Override
    public void pauseProcess() throws AsynchronousOperationException {
        // TODO: Check if called outside of receiver call after completion of all asynchronous operations
        this.interrupted = true;
        ++this.asyncOpDepth;
    }

    @Override
    public void continueProcess(final Throwable e)
            throws AsynchronousOperationException {
        int asOp = --this.asyncOpDepth;

        if (asOp < 0) {
            throw new AsynchronousOperationException("Too many calls of #continueProcess.");
        }

        if (null != e) {
            if (null != asyncException) {
                e.addSuppressed(asyncException);
            }

            asyncException = e;
        }

        if (asOp == 0) {
            if (null != asyncException) {
                try {
                    messageProcessingSequence.catchException(asyncException, context);
                } catch (Exception e1) {
                    throw new AsynchronousOperationException(
                            "Exception occurred while processing exceptional completion of operation.", e1);
                }
            }

            enqueueNext();
        }
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
    public IObject getEnvironment() {
        return environment;
    }

    @Override
    public void setConfig(final IObject config) throws InvalidArgumentException {
        if (null == config) {
            throw new InvalidArgumentException("Configuration object should not be null.");
        }
        this.config = config;
    }

    @Override
    public void execute() throws TaskExecutionException {
        try {
            try {
                this.interrupted = false;
                this.asyncOpDepth = 0;
                this.asyncException = null;
                this.environment.setValue(argumentsFieldName, messageProcessingSequence.getCurrentReceiverArguments());
                messageProcessingSequence.getCurrentReceiver().receive(this);
            } catch (Throwable e) {
                messageProcessingSequence.catchException(e, context);
            }

            if (!interrupted) {
                enqueueNext();
            }
        } catch (final Exception e1) {
            complete();
            throw new TaskExecutionException("Exception occurred while handling exception occurred in message receiver.", e1);
        }
    }

    private void enqueueNext() {
        if (messageProcessingSequence.next()) {
            enqueue();
        }
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
