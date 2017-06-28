package info.smart_tools.smartactors.http_endpoint.environment_handler;


import info.smart_tools.smartactors.iobject.field_name.FieldName;
import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.endpoint.interfaces.ienvironment_handler.IEnvironmentHandler;
import info.smart_tools.smartactors.endpoint.interfaces.ienvironment_handler.exception.EnvironmentHandleException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.MessageProcessorProcessException;
import info.smart_tools.smartactors.task.interfaces.iqueue.IQueue;
import info.smart_tools.smartactors.task.interfaces.itask.ITask;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessingSequence;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IReceiverChain;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;

/**
 * Realization of {@link IEnvironmentHandler} with using {@link IOC}
 */
public class EnvironmentHandler implements IEnvironmentHandler {
    private final IQueue<ITask> taskQueue;
    private final int stackDepth;

    private final IFieldName messageFieldName;
    private final IFieldName contextFieldName;
    private final IFieldName fromExternalFieldName;

    /**
     * Handler for environment from endpoint
     * @param taskQueue Queue of the tasks
     * @param stackDepth Stack depth of the {@link IMessageProcessor}
     * @throws InvalidArgumentException if there is invalid arguments
     */
    public EnvironmentHandler(final IQueue<ITask> taskQueue, final int stackDepth)
            throws InvalidArgumentException, ResolutionException {
        this.messageFieldName = IOC.resolve(Keys.getOrAdd(FieldName.class.getCanonicalName()), "message");
        this.contextFieldName = IOC.resolve(Keys.getOrAdd(FieldName.class.getCanonicalName()), "context");
        this.fromExternalFieldName = IOC.resolve(Keys.getOrAdd(FieldName.class.getCanonicalName()), "fromExternal");
        if (null == taskQueue) {
            throw new InvalidArgumentException("Task queue should not be null.");
        }
        if (stackDepth < 0) {
            throw new InvalidArgumentException("Stack depth should be positive number.");
        }
        this.stackDepth = stackDepth;
        this.taskQueue = taskQueue;
    }

    public void handle(final IObject environment, final IReceiverChain receiverChain, final IAction<Throwable> callback)
            throws EnvironmentHandleException {
        try {
            IMessageProcessingSequence processingSequence =
                    IOC.resolve(Keys.getOrAdd(IMessageProcessingSequence.class.getCanonicalName()), stackDepth, receiverChain);
            IMessageProcessor messageProcessor =
                    IOC.resolve(Keys.getOrAdd(IMessageProcessor.class.getCanonicalName()), taskQueue, processingSequence);

            IObject message = (IObject) environment.getValue(this.messageFieldName);
            IObject context = (IObject) environment.getValue(this.contextFieldName);
            context.setValue(this.fromExternalFieldName, true);
            messageProcessor.process(message, context);
        } catch (ResolutionException | InvalidArgumentException | ReadValueException | ChangeValueException
                | MessageProcessorProcessException e) {
            throw new EnvironmentHandleException(e);
        }
    }
}
