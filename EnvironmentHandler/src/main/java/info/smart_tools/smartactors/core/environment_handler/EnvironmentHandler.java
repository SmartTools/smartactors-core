package info.smart_tools.smartactors.core.environment_handler;


import info.smart_tools.smartactors.core.field_name.FieldName;
import info.smart_tools.smartactors.core.ienvironment_handler.IEnvironmentHandler;
import info.smart_tools.smartactors.core.ifield_name.IFieldName;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.iqueue.IQueue;
import info.smart_tools.smartactors.core.itask.ITask;
import info.smart_tools.smartactors.core.message_processing.IMessageProcessingSequence;
import info.smart_tools.smartactors.core.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.core.message_processing.IReceiverChain;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;

/**
 * Realization of {@link IEnvironmentHandler} with using {@link IOC}
 */
public class EnvironmentHandler implements IEnvironmentHandler {
    private final IQueue<ITask> taskQueue;
    private final int stackDepth;

    /**
     * Handler for environment from endpoint
     * @param taskQueue Queue of the tasks
     * @param stackDepth Stack depth of the {@link IMessageProcessor}
     * @throws InvalidArgumentException if there is invalid arguments
     */
    public EnvironmentHandler(final IQueue<ITask> taskQueue, final int stackDepth) throws InvalidArgumentException {
        if (null == taskQueue) {
            throw new InvalidArgumentException("Task queue should not be null.");
        }
        if (stackDepth < 0) {
            throw new InvalidArgumentException("Stack depth should be positive number.");
        }
        this.stackDepth = stackDepth;
        this.taskQueue = taskQueue;
    }

    @Override
    public void handle(final IObject environment, final IReceiverChain receiverChain) {
        try {
            IMessageProcessingSequence processingSequence =
                    IOC.resolve(Keys.getOrAdd(IMessageProcessingSequence.class.toString()), stackDepth, receiverChain);
            IMessageProcessor messageProcessor =
                    IOC.resolve(Keys.getOrAdd(IMessageProcessor.class.toString()), taskQueue, processingSequence);
            IFieldName messageFieldName = IOC.resolve(Keys.getOrAdd(FieldName.class.toString()), "message");
            IFieldName contextFieldName = IOC.resolve(Keys.getOrAdd(FieldName.class.toString()), "context");
            IObject message = IOC.resolve(Keys.getOrAdd(IObject.class.toString()), environment.getValue(messageFieldName));
            IObject context = IOC.resolve(Keys.getOrAdd(IObject.class.toString()), environment.getValue(contextFieldName));
            messageProcessor.process(message, context);
        } catch (ResolutionException | InvalidArgumentException | ReadValueException | ChangeValueException e) {
        }
    }
}
