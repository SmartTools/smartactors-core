package info.smart_tools.smartactors.message_processing.message_processor;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessingSequence;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.AsynchronousOperationException;
import info.smart_tools.smartactors.task.interfaces.iqueue.IQueue;
import info.smart_tools.smartactors.task.interfaces.itask.ITask;
import info.smart_tools.smartactors.task.interfaces.itask.exception.TaskExecutionException;

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
    private IObject rawEnvironment;
    private IObject currentEnvironment;

    private final IFieldName configFieldName;
    private final IFieldName messageFieldName;
    private final IFieldName contextFieldName;
    private final IFieldName responseFieldName;
    private final IFieldName sequenceFieldName;
    private final IFieldName argumentsFieldName;
    private final IFieldName processorFieldName;

    private ITask finalTask;

    /**
     * True if processing was interrupted (using {@link #pauseProcess()}) during execution of last receiver.
     */
    private int interrupted;

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

        this.interrupted = 0;
        this.asyncOpDepth = 0;

        this.rawEnvironment = IOC.resolve(IOC.resolve(IOC.getKeyForKeyStorage(), IObject.class.getCanonicalName()));

        configFieldName = IOC.resolve(IOC.resolve(IOC.getKeyForKeyStorage(), IFieldName.class.getCanonicalName()), "config");
        messageFieldName = IOC.resolve(IOC.resolve(IOC.getKeyForKeyStorage(), IFieldName.class.getCanonicalName()), "message");
        contextFieldName = IOC.resolve(IOC.resolve(IOC.getKeyForKeyStorage(), IFieldName.class.getCanonicalName()), "context");
        responseFieldName = IOC.resolve(IOC.resolve(IOC.getKeyForKeyStorage(), IFieldName.class.getCanonicalName()), "response");
        sequenceFieldName = IOC.resolve(IOC.resolve(IOC.getKeyForKeyStorage(), IFieldName.class.getCanonicalName()), "sequence");
        argumentsFieldName = IOC.resolve(IOC.resolve(IOC.getKeyForKeyStorage(), IFieldName.class.getCanonicalName()), "arguments");
        processorFieldName = IOC.resolve(IOC.resolve(IOC.getKeyForKeyStorage(), IFieldName.class.getCanonicalName()), "processor");

        this.finalTask = IOC.resolve(IOC.resolve(IOC.getKeyForKeyStorage(), "final task"), this.rawEnvironment);
    }

    @Override
    public void process(final IObject theMessage, final IObject theContext)
            throws InvalidArgumentException, ResolutionException, ChangeValueException {
        // TODO: Ensure that there is no process in progress
        this.message = theMessage;
        this.context = theContext;
        this.response = IOC.resolve(IOC.resolve(IOC.getKeyForKeyStorage(), IObject.class.getCanonicalName()));

        rawEnvironment.setValue(configFieldName, config);
        rawEnvironment.setValue(sequenceFieldName, messageProcessingSequence);
        rawEnvironment.setValue(responseFieldName, response);
        rawEnvironment.setValue(messageFieldName, theMessage);
        rawEnvironment.setValue(contextFieldName, theContext);
        rawEnvironment.setValue(processorFieldName, this);

        currentEnvironment = rawEnvironment;

        enqueue();
    }

    @Override
    public void pauseProcess() throws AsynchronousOperationException {
        // TODO: Check if called outside of receiver call after completion of all asynchronous operations
        ++this.interrupted;
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
        return currentEnvironment;
    }

    @Override
    public void pushEnvironment(final IObject newEnvironment) throws InvalidArgumentException {
        if (null == newEnvironment) {
            throw new InvalidArgumentException("Environment is null.");
        }

        this.currentEnvironment = newEnvironment;
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
            int initialInt = this.interrupted;
            try {
                this.asyncOpDepth = 0;
                this.asyncException = null;
                this.rawEnvironment.setValue(argumentsFieldName, messageProcessingSequence.getCurrentReceiverArguments());

                currentEnvironment = rawEnvironment;

                messageProcessingSequence.getCurrentReceiver().receive(this);
            } catch (Throwable e) {
                messageProcessingSequence.catchException(e, context);
            }

            if (interrupted == initialInt) {
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
        } else {
            this.complete();
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
        try {
            this.taskQueue.put(this.finalTask);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        this.messageProcessingSequence.reset();
        // TODO: Return message, context, response and {@code this} to the pool
    }
}
