package info.smart_tools.smartactors.http_endpoint.http_response_handler;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.endpoint.interfaces.ichannel_handler.IChannelHandler;
import info.smart_tools.smartactors.endpoint.interfaces.ideserialize_strategy.IDeserializeStrategy;
import info.smart_tools.smartactors.endpoint.interfaces.ideserialize_strategy.exceptions.DeserializationException;
import info.smart_tools.smartactors.endpoint.interfaces.iresponse_handler.IResponseHandler;
import info.smart_tools.smartactors.endpoint.interfaces.iresponse_handler.exception.ResponseHandlerException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.ichain_storage.IChainStorage;
import info.smart_tools.smartactors.message_processing_interfaces.ichain_storage.exceptions.ChainNotFoundException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessingSequence;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IReceiverChain;
import info.smart_tools.smartactors.scope.iscope.IScope;
import info.smart_tools.smartactors.scope.iscope_provider_container.exception.ScopeProviderException;
import info.smart_tools.smartactors.scope.scope_provider.ScopeProvider;
import info.smart_tools.smartactors.task.interfaces.iqueue.IQueue;
import info.smart_tools.smartactors.task.interfaces.itask.ITask;
import info.smart_tools.smartactors.task.interfaces.itask.exception.TaskExecutionException;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;

import java.util.List;

/**
 * Handler for http response
 */
public class HttpResponseHandler implements IResponseHandler<ChannelHandlerContext, FullHttpResponse> {
    private IQueue<ITask> taskQueue;
    private int stackDepth;
    private IReceiverChain receiverChain;
    private IObject request;
    private IFieldName messageFieldName;
    private IFieldName contextFieldName;
    private IFieldName httpResponseStatusCodeFieldName;
    private IFieldName responseFieldName;
    private IFieldName requestFieldName;
    private IFieldName messageMapIdFieldName;
    private IFieldName uuidFieldName;
    private Object messageMapId;
    private Object uuid;
    private boolean isReceived;
    private IScope currentScope;

    /**
     * Constructor
     *  @param taskQueue     main queue of the {@link ITask}
     * @param stackDepth    depth of the stack for {@link io.netty.channel.ChannelOutboundBuffer.MessageProcessor}
     * @param receiverChain chain, that should receive message
     */
    public HttpResponseHandler(final IQueue<ITask> taskQueue, final int stackDepth, final Object receiverChain,
                               final IObject request, final IScope scope) throws ResponseHandlerException {
        this.taskQueue = taskQueue;
        this.stackDepth = stackDepth;

        try {
            IChainStorage chainStorage = IOC.resolve(IOC.resolve(IOC.getKeyForKeyStorage(),
                    IChainStorage.class.getCanonicalName()));
            Object mapId = IOC.resolve(Keys.getOrAdd("chain_id_from_map_name"), receiverChain);
            this.receiverChain = chainStorage.resolve(mapId);
            messageFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "message");
            contextFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "context");
            httpResponseStatusCodeFieldName = IOC.resolve(
                    Keys.getOrAdd(IFieldName.class.getCanonicalName()),
                    "httpResponseStatusCode"
            );
            responseFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "response");
            requestFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "sendRequest");
            messageMapIdFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "messageMapId");
            uuidFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "uuid");
            this.request = request;
            this.messageMapId = request.getValue(messageMapIdFieldName);
            this.uuid = request.getValue(uuidFieldName);
            isReceived = false;
            currentScope = scope;
        } catch (ResolutionException | InvalidArgumentException | ReadValueException | ChainNotFoundException e) {
            throw new ResponseHandlerException(e);
        }
    }

    @Override
    public void handle(final ChannelHandlerContext ctx, final FullHttpResponse response) throws ResponseHandlerException {
        try {
            ScopeProvider.setCurrentScope(currentScope);
            try {
                IOC.resolve(Keys.getOrAdd("cancelTimerOnRequest"), uuid);
            } catch (ResolutionException e) {
                // Timeout
                return;
            }
            isReceived = true;
            FullHttpResponse responseCopy = response.copy();
            ITask task = () -> {
                try {
                    IObject environment = getEnvironment(responseCopy);
                    IMessageProcessingSequence processingSequence =
                            IOC.resolve(Keys.getOrAdd(IMessageProcessingSequence.class.getCanonicalName()), stackDepth, receiverChain);
                    IMessageProcessor messageProcessor =
                            IOC.resolve(Keys.getOrAdd(IMessageProcessor.class.getCanonicalName()), taskQueue, processingSequence);
                    IFieldName messageFieldName = null;
                    messageFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "message");
                    IFieldName contextFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "context");
                    IObject message = (IObject) environment.getValue(messageFieldName);
                    message.setValue(messageMapIdFieldName, messageMapId);
                    IObject context = (IObject) environment.getValue(contextFieldName);
                    messageProcessor.process(message, context);
                } catch (ChangeValueException | ReadValueException | InvalidArgumentException | ResponseHandlerException |
                        ResolutionException e) {
                    throw new TaskExecutionException(e);
                }
            };
            taskQueue.put(task);
        } catch (ScopeProviderException | InterruptedException e) {
            throw new ResponseHandlerException(e);
        }

    }

    private IObject getEnvironment(final FullHttpResponse response) throws ResponseHandlerException {
        try {
            IDeserializeStrategy deserializeStrategy = IOC.resolve(Keys.getOrAdd("httpResponseResolver"), response);
            IObject message = deserializeStrategy.deserialize(response);
            IObject environment = IOC.resolve(Keys.getOrAdd("EmptyIObject"));
            IObject context = IOC.resolve(Keys.getOrAdd("EmptyIObject"));
            context.setValue(responseFieldName, response);
            context.setValue(httpResponseStatusCodeFieldName, response.status().code());
            context.setValue(requestFieldName, this.request);
            environment.setValue(messageFieldName, message);
            environment.setValue(contextFieldName, context);
            return environment;
        } catch (ResolutionException | DeserializationException | ChangeValueException | InvalidArgumentException e) {
            throw new ResponseHandlerException(e);
        }
    }
}
