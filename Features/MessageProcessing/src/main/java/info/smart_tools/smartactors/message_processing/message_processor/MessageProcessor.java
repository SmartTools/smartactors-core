package info.smart_tools.smartactors.message_processing.message_processor;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.base.iup_counter.IUpCounter;
import info.smart_tools.smartactors.base.iup_counter.exception.IllegalUpCounterState;
import info.smart_tools.smartactors.base.iup_counter.exception.UpCounterCallbackExecutionException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessingSequence;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.Signal;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.*;
import info.smart_tools.smartactors.scope.iscope_provider_container.exception.ScopeProviderException;
import info.smart_tools.smartactors.shutdown.ishutdown_aware_task.IShutdownAwareTask;
import info.smart_tools.smartactors.shutdown.ishutdown_aware_task.exceptions.ShutdownAwareTaskNotificationException;
import info.smart_tools.smartactors.task.imanaged_task.IManagedTask;
import info.smart_tools.smartactors.task.interfaces.iqueue.IQueue;
import info.smart_tools.smartactors.task.interfaces.itask.ITask;
import info.smart_tools.smartactors.task.interfaces.itask.exception.TaskExecutionException;

import java.util.ArrayList;
import java.util.List;

/**
 * Task that performs on a message actions defined by a message processing sequence.
 *
 * @see IMessageProcessingSequence
 * @see ITask
 */
public class MessageProcessor implements ITask, IMessageProcessor, IManagedTask, IShutdownAwareTask {
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

    private final IFieldName finalActionsFieldName;

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

    /**
     * A throwable representing the last received but not processed signal.
     */
    private Signal signalException;

    private final IQueue<ITask> taskQueue;
    private final IMessageProcessingSequence messageProcessingSequence;

    private IUpCounter upCounter;
    private boolean notifiedOnShutdown = false;
    private Object shutdownStatus;

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

        this.rawEnvironment = IOC.resolve(IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.iobject.IObject"));

        configFieldName = IOC.resolve(IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "config");
        messageFieldName = IOC.resolve(IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "message");
        contextFieldName = IOC.resolve(IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "context");
        responseFieldName = IOC.resolve(IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "response");
        sequenceFieldName = IOC.resolve(IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "sequence");
        argumentsFieldName = IOC.resolve(IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "arguments");
        processorFieldName = IOC.resolve(IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "processor");
        finalActionsFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "finalActions");

        this.finalTask = IOC.resolve(IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "final task"), this.rawEnvironment);

        this.upCounter = IOC.resolve(Keys.getKeyByName("root upcounter"));
    }

    @Override
    public void process(final IObject theMessage, final IObject theContext)
            throws InvalidArgumentException, MessageProcessorProcessException {
        // TODO: Ensure that there is no process in progress
        this.message = theMessage;
        this.context = theContext;

        try {
            this.response = IOC.resolve(IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.iobject.IObject"));

            rawEnvironment.setValue(configFieldName, config);
            rawEnvironment.setValue(sequenceFieldName, messageProcessingSequence);
            rawEnvironment.setValue(responseFieldName, response);
            rawEnvironment.setValue(messageFieldName, theMessage);
            rawEnvironment.setValue(contextFieldName, theContext);
            rawEnvironment.setValue(processorFieldName, this);

            currentEnvironment = rawEnvironment;

            List finalActionsList = (List) context.getValue(finalActionsFieldName);

            if (null == finalActionsList) {
                finalActionsList = new ArrayList(1);
                context.setValue(finalActionsFieldName, finalActionsList);
            }

            finalActionsList.add((IAction<IObject>) env -> {
                try {
                    upCounter.down();
                } catch (UpCounterCallbackExecutionException | IllegalUpCounterState e) {
                    throw new ActionExecutionException(e);
                }
            });

            upCounter.up();

            notifiedOnShutdown = false;
            shutdownStatus = null;
        } catch (ReadValueException | ChangeValueException | InvalidArgumentException | ResolutionException | IllegalUpCounterState e) {
            throw new MessageProcessorProcessException(e);
        }

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
            try {
                if (null != asyncException) {
                    messageProcessingSequence.catchException(asyncException, context);
                }

                enqueueNext();
            } catch (Exception e1) {
                throw new AsynchronousOperationException(
                        "Exception occurred while processing exceptional completion of operation.", e1);
            }
        }
    }

    @Override
    public void signal(final String signalName) throws InvalidArgumentException {
        if (null == signalName) {
            throw new InvalidArgumentException("Signal name should not be null.");
        }

        try {
            Object signal = IOC.resolve(Keys.getKeyByName(signalName));

            if (!(signal instanceof Signal)) {
                throw new InvalidArgumentException("Resolved signal is not a signal.");
            }

            this.signalException = (Signal) signal;
        } catch (ResolutionException e) {
            throw new InvalidArgumentException("Could not resolve a signal throwable.", e);
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
    public void resetEnvironment() {
        currentEnvironment = rawEnvironment;
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

    private void enqueueNext() throws NestedChainStackOverflowException, InvalidArgumentException,
            ChangeValueException, ReadValueException, ResolutionException, ChainNotFoundException, ScopeProviderException {
        checkSignal();

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

    private void checkSignal()
            throws ChangeValueException, InvalidArgumentException, NestedChainStackOverflowException,
            ReadValueException, ResolutionException, ChainNotFoundException, ScopeProviderException {
        Throwable signal = this.signalException;

        if (null != signal) {
            try {
                messageProcessingSequence.catchException(signal, context);
            } catch (NoExceptionHandleChainException e) {
                messageProcessingSequence.end();
            }

            this.signalException = null;
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

    @Override
    public <T> T getAs(final Class<T> clazz) throws InvalidArgumentException {
        if (!clazz.isInstance(this)) {
            throw new InvalidArgumentException("Cannot represent message processor as " + clazz.getCanonicalName());
        }

        return (T) this;
    }

    @Override
    public void notifyShuttingDown() throws ShutdownAwareTaskNotificationException {
        if (!notifiedOnShutdown) {
            notifiedOnShutdown = true;

            try {
                signal("shutdown signal");
            } catch (InvalidArgumentException e) {
                throw new ShutdownAwareTaskNotificationException(e);
            }
        }
    }

    @Override
    public void notifyIgnored() throws ShutdownAwareTaskNotificationException {
        complete();
    }

    @Override
    public void setShutdownStatus(final Object status) {
        this.shutdownStatus = status;
    }

    @Override
    public Object getShutdownStatus() {
        return this.shutdownStatus;
    }
}
