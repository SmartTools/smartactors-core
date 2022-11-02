package info.smart_tools.smartactors.endpoint.actor.client;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.endpoint.actor.client.exception.RequestSenderActorException;
import info.smart_tools.smartactors.endpoint.actor.client.wrapper.ClientActorMessage;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.iresponse_strategy.IResponseStrategy;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessingSequence;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.MessageProcessorProcessException;
import info.smart_tools.smartactors.task.interfaces.iqueue.IQueue;
import info.smart_tools.smartactors.task.interfaces.itask.ITask;

import java.util.UUID;

/**
 * Actor for sending requests to other servers
 */
public class ClientActor {

    private IFieldName queueFieldName;
    private IFieldName stackDepthFieldName;
    private IFieldName responseStrategyFieldName;
    private IFieldName uidFieldName;
    private IKey messageProcessingSequenceKey;
    private IKey messageProcessorKey;
    private IKey responseHandlerConfigurationKey;
    private IKey iObjectKey;
    private IKey nullResponseStrategyKey;

    /**
     * Constructor for actor
     */
    public ClientActor() {
        try {
            queueFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "queue");
            stackDepthFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "stackDepth");
            responseStrategyFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "responseStrategy");
            uidFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "uid");
            messageProcessingSequenceKey = Keys.getKeyByName("info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessingSequence");
            messageProcessorKey = Keys.getKeyByName("info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor");
            responseHandlerConfigurationKey = Keys.getKeyByName("responseHandlerConfiguration");
            iObjectKey = Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject");
            nullResponseStrategyKey = Keys.getKeyByName("null response strategy");
        } catch (ResolutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Handler of the actor for send response
     *
     * @param message Wrapper of the actor
     * @throws RequestSenderActorException if there are some problems on sending response
     */
    public void sendRequest(final ClientActorMessage message)
            throws RequestSenderActorException {
        try {
            IObject request = message.getRequest();
            request.setValue(uidFieldName, UUID.randomUUID().toString());

            IObject clientConfig = IOC.resolve(responseHandlerConfigurationKey);
            Integer stackDepth = (Integer)clientConfig.getValue(stackDepthFieldName);
            IMessageProcessingSequence processingSequence = IOC.resolve(
                    messageProcessingSequenceKey,
                    stackDepth,
                    message.getSendingChain(),
                    request,
                    true
            );
            IQueue<ITask> taskQueue = (IQueue<ITask>)clientConfig.getValue(queueFieldName);
            IMessageProcessor messageProcessor =  IOC.resolve(
                    messageProcessorKey,
                    taskQueue,
                    processingSequence
            );
            IResponseStrategy nullResponseStrategy = IOC.resolve(nullResponseStrategyKey);
            IObject context = IOC.resolve(iObjectKey);
            context.setValue(responseStrategyFieldName, nullResponseStrategy);
            messageProcessor.process(request, context);
        } catch (ReadValueException | ResolutionException | MessageProcessorProcessException
                | InvalidArgumentException | ChangeValueException e) {
            throw new RequestSenderActorException(e);
        }
    }
}
