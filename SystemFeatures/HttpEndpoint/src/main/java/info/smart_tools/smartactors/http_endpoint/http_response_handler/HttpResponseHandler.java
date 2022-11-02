package info.smart_tools.smartactors.http_endpoint.http_response_handler;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.class_management.interfaces.imodule.IModule;
import info.smart_tools.smartactors.class_management.module_manager.ModuleManager;
import info.smart_tools.smartactors.endpoint.interfaces.ideserialize_strategy.IDeserializeStrategy;
import info.smart_tools.smartactors.endpoint.interfaces.ideserialize_strategy.exceptions.DeserializationException;
import info.smart_tools.smartactors.endpoint.interfaces.iresponse_handler.IResponseHandler;
import info.smart_tools.smartactors.endpoint.interfaces.iresponse_handler.exception.ResponseHandlerException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessingSequence;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.MessageProcessorProcessException;
import info.smart_tools.smartactors.scope.iscope.IScope;
import info.smart_tools.smartactors.scope.iscope_provider_container.exception.ScopeProviderException;
import info.smart_tools.smartactors.scope.scope_provider.ScopeProvider;
import info.smart_tools.smartactors.task.interfaces.iqueue.IQueue;
import info.smart_tools.smartactors.task.interfaces.itask.ITask;
import info.smart_tools.smartactors.task.interfaces.itask.exception.TaskExecutionException;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;

import java.util.ArrayList;

/**
 * Handler for http response
 */
public class HttpResponseHandler implements IResponseHandler<ChannelHandlerContext, FullHttpResponse> {
    private IQueue<ITask> taskQueue;
    private int stackDepth;
    private Object receiverMapName;
    private IObject request;
    private IFieldName messageFieldName;
    private IFieldName contextFieldName;
    private IFieldName httpResponseStatusCodeFieldName;
    private IFieldName responseFieldName;
    private IFieldName headersFieldName;
    private IFieldName cookiesFieldName;
    private IFieldName requestFieldName;
    private IFieldName messageMapIdFieldName;
    private IFieldName uuidFieldName;
    private Object messageMapId;
    private Object uuid;
    private boolean isReceived;
    private IScope currentScope;
    private IModule currentModule;

    /**
     * Constructor
     *
     * @param taskQueue     main queue of the {@link ITask}
     * @param stackDepth    depth of the stack for {@link io.netty.channel.ChannelOutboundBuffer.MessageProcessor}
     * @param mapName chain, that should receive message
     * @param request request
     * @param scope scope
     * @param module module
     *
     * @throws ResponseHandlerException if any errors occurred
     */
    public HttpResponseHandler(
            final IQueue<ITask> taskQueue,
            final int stackDepth,
            final Object mapName,
            final IObject request,
            final IScope scope,
            IModule module
    ) throws ResponseHandlerException {
        this.taskQueue = taskQueue;
        this.stackDepth = stackDepth;

        try {
            receiverMapName = mapName;
            messageFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "message");
            contextFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "context");
            httpResponseStatusCodeFieldName = IOC.resolve(
                    Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"),
                    "httpResponseStatusCode"
            );
            responseFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "response");
            headersFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "headers");
            cookiesFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "cookies");
            requestFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "sendRequest");
            messageMapIdFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "messageMapId");
            uuidFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "uuid");
            this.request = request;
            this.messageMapId = request.getValue(messageMapIdFieldName);
            this.uuid = request.getValue(uuidFieldName);
            isReceived = false;
            currentScope = scope;
            currentModule = module;
        } catch (ResolutionException | InvalidArgumentException | ReadValueException e) {
            throw new ResponseHandlerException(e);
        }
    }

    @Override
    public void handle(final ChannelHandlerContext ctx, final FullHttpResponse response) throws ResponseHandlerException {
        try {
            ScopeProvider.setCurrentScope(currentScope);
            ModuleManager.setCurrentModule(currentModule);
            IOC.resolve(Keys.getKeyByName("cancelTimerOnRequest"), uuid);
            isReceived = true;
            FullHttpResponse responseCopy = response.copy();
            ITask task = () -> {
                try {
                    IObject environment = getEnvironment(responseCopy);
                    IObject message = (IObject) environment.getValue(messageFieldName);
                    IMessageProcessingSequence processingSequence =
                            IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessingSequence"), stackDepth, receiverMapName, message);
                    IMessageProcessor messageProcessor =
                            IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor"), taskQueue, processingSequence);
                    message.setValue(messageMapIdFieldName, messageMapId);
                    IObject context = (IObject) environment.getValue(contextFieldName);
                    messageProcessor.process(message, context);
                } catch (ChangeValueException | ReadValueException | InvalidArgumentException | ResponseHandlerException |
                        ResolutionException | MessageProcessorProcessException e) {
                    throw new TaskExecutionException(e);
                }
            };
            taskQueue.put(task);
        } catch (ScopeProviderException | InterruptedException | ResolutionException e) {
            throw new ResponseHandlerException(e);
        }

    }

    private IObject getEnvironment(final FullHttpResponse response) throws ResponseHandlerException {
        try {
            IDeserializeStrategy deserializeStrategy = IOC.resolve(Keys.getKeyByName("httpResponseResolver"), response);
            IObject message = deserializeStrategy.deserialize(response);
            IObject environment = IOC.resolve(Keys.getKeyByName("EmptyIObject"));
            IObject context = IOC.resolve(Keys.getKeyByName("EmptyIObject"));
            context.setValue(cookiesFieldName, new ArrayList<IObject>());
            context.setValue(headersFieldName, new ArrayList<IObject>());
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
