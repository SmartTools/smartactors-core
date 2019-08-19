package info.smart_tools.smartactors.http_endpoint.environment_handler;


import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.endpoint.interfaces.ienvironment_handler.IEnvironmentHandler;
import info.smart_tools.smartactors.endpoint.interfaces.ienvironment_handler.exception.EnvironmentHandleException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessingSequence;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.MessageProcessorProcessException;
import info.smart_tools.smartactors.task.interfaces.iqueue.IQueue;
import info.smart_tools.smartactors.task.interfaces.itask.ITask;

/**
 * Realization of {@link IEnvironmentHandler} with using {@link IOC}
 */
public class EnvironmentHandler implements IEnvironmentHandler {
    private final IQueue<ITask> taskQueue;
    private final int stackDepth;

    private final IFieldName messageFieldName;
    private final IFieldName contextFieldName;
    private final IFieldName fromExternalFieldName;
    private final Boolean scopeSwitching;

    /**
     * Handler for environment from endpoint
     * @param taskQueue Queue of the tasks
     * @param stackDepth Stack depth of the {@link IMessageProcessor}
     * @throws InvalidArgumentException if there is invalid arguments
     */
    public EnvironmentHandler(final IQueue<ITask> taskQueue, final int stackDepth, final Boolean scopeSwitching)
            throws InvalidArgumentException, ResolutionException {
        this.messageFieldName = IOC.resolve(Keys.getKeyByName(IFieldName.class.getCanonicalName()), "message");
        this.contextFieldName = IOC.resolve(Keys.getKeyByName(IFieldName.class.getCanonicalName()), "context");
        this.fromExternalFieldName = IOC.resolve(Keys.getKeyByName(IFieldName.class.getCanonicalName()), "fromExternal");
        if (null == taskQueue) {
            throw new InvalidArgumentException("Task queue should not be null.");
        }
        if (stackDepth < 0) {
            throw new InvalidArgumentException("Stack depth should be positive number.");
        }
        this.stackDepth = stackDepth;
        this.taskQueue = taskQueue;
        this.scopeSwitching = scopeSwitching;
    }

    public void handle(final IObject environment, final Object receiverChainName, final IAction<Throwable> callback)
            throws EnvironmentHandleException {
        try {
            IObject message = (IObject) environment.getValue(this.messageFieldName);
            IObject context = (IObject) environment.getValue(this.contextFieldName);
            IMessageProcessingSequence processingSequence =
                    IOC.resolve(Keys.getKeyByName(
                            "info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessingSequence"),
                            stackDepth,
                            receiverChainName,
                            message,
                            this.scopeSwitching
                    );
            IMessageProcessor messageProcessor =
                    IOC.resolve(
                            Keys.getKeyByName("info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor"),
                            taskQueue,
                            processingSequence
                    );

            context.setValue(this.fromExternalFieldName, true);
            messageProcessor.process(message, context);
        } catch (ResolutionException | InvalidArgumentException | ReadValueException | ChangeValueException
                | MessageProcessorProcessException e) {
            throw new EnvironmentHandleException(e);
        }
    }
}
