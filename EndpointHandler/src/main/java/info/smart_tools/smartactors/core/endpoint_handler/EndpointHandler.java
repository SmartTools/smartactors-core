package info.smart_tools.smartactors.core.endpoint_handler;

import info.smart_tools.smartactors.core.endpoint_handler.exceptions.EndpointException;
import info.smart_tools.smartactors.core.ienvironment_extractor.IEnvironmentExtractor;
import info.smart_tools.smartactors.core.ienvironment_handler.IEnvironmentHandler;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.iqueue.IQueue;
import info.smart_tools.smartactors.core.iscope.IScope;
import info.smart_tools.smartactors.core.iscope_provider_container.exception.ScopeProviderException;
import info.smart_tools.smartactors.core.itask.ITask;
import info.smart_tools.smartactors.core.message_processing.IReceiverChain;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.core.scope_provider.ScopeProvider;
import io.netty.handler.codec.http.FullHttpRequest;

import java.util.concurrent.ExecutionException;

/**
 * Base class with a common message handling logic from endpoints.
 * It will initiate message contexts, generate service fields etc...
 *
 * @param <TContext> type of context associated with endpoint data channel.
 *                   It is used to avoid unnecessary new instances creation in endpoint.
 * @param <TRequest> type of a request received by endpoint
 */
public abstract class EndpointHandler<TContext, TRequest> {
    private final IReceiverChain receiverChain;
    private final IEnvironmentHandler environmentHandler;
    private final IScope scope;
    private final IQueue<ITask> taskQueue;
    private final IEnvironmentExtractor environmentExtractor;

    /**
     * Constructor for HttpRequestHandler
     *
     * @param scope              scope for HttpRequestHandler
     * @param environmentHandler handler for environment
     * @param receiver           chain, that should receive message
     * @throws EndpointException if there are problems on constructing
     */
    public EndpointHandler(final IReceiverChain receiver, final IEnvironmentHandler environmentHandler,
                           final IScope scope) throws EndpointException {
        this.scope = scope;
        this.receiverChain = receiver;
        this.environmentHandler = environmentHandler;
        try {
            ScopeProvider.setCurrentScope(scope);
            this.taskQueue = IOC.resolve(Keys.getOrAdd("task_queue"));
            environmentExtractor =
                    IOC.resolve(Keys.getOrAdd(IEnvironmentExtractor.class.getCanonicalName()));
        } catch (ResolutionException e) {
            throw new EndpointException("Failed to resolve queue of the tasks", e);
        } catch (ScopeProviderException e) {
            throw new EndpointException("Failed to set scope at endpoint handler", e);
        }
    }

    /**
     * Parse a message from the given request.
     * Endpoint can receive message in different formats, so we can't make this process common for now.
     *
     * @param request request to the endpoint
     * @param ctx     context of the request
     * @return deserialized message
     * @throws Exception if there is exception at environment getting
     */
    public abstract IObject getEnvironment(TContext ctx, TRequest request) throws Exception;

    /**
     * Handle an endpoint request using the specified context.
     *
     * @param ctx     endpoint channel context
     * @param request request to the endpoint
     * @throws ExecutionException if request failed to handle
     */
    public void handle(final TContext ctx, final TRequest request) throws ExecutionException {
        try {
            ScopeProvider.setCurrentScope(scope);
            TRequest buffRequest = (TRequest) ((FullHttpRequest) request).copy();
            ITask endpointTask = IOC.resolve(Keys.getOrAdd(EndpointHandlerTask.class.getCanonicalName()),
                    environmentExtractor, ctx, buffRequest, environmentHandler, receiverChain);
            taskQueue.put(endpointTask);
        } catch (ResolutionException | ScopeProviderException | InterruptedException e) {
            throw new ExecutionException("Failed to put task to queue of the tasks", e);
        }
    }
}
