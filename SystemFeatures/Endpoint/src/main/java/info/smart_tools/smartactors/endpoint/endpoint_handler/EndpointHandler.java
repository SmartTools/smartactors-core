package info.smart_tools.smartactors.endpoint.endpoint_handler;

import info.smart_tools.smartactors.class_management.interfaces.imodule.IModule;
import info.smart_tools.smartactors.class_management.module_manager.ModuleManager;
import info.smart_tools.smartactors.endpoint.interfaces.ienvironment_handler.IEnvironmentHandler;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.scope.iscope.IScope;
import info.smart_tools.smartactors.scope.scope_provider.ScopeProvider;
import info.smart_tools.smartactors.task.interfaces.iqueue.IQueue;
import info.smart_tools.smartactors.task.interfaces.itask.ITask;
import info.smart_tools.smartactors.task.interfaces.itask.exception.TaskExecutionException;

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
    private final Object receiverChainName;
    private final IEnvironmentHandler environmentHandler;
    private final IScope scope;
    private final IModule module;
    private final String name;

    private final IQueue<ITask> taskQueue;

    /**
     * Constructor for HttpRequestHandler
     *
     * @param scope              scope for EndpointHandler
     * @param module             the module in which context EndpointHandler works
     * @param environmentHandler handler for environment
     * @param receiverName       chain name of chain that should receive message
     * @param name               name of the endpoint
     * @throws ResolutionException if error occurs resolving any dependencies
     */
    public EndpointHandler(final Object receiverName, final IEnvironmentHandler environmentHandler,
                           final IScope scope, final IModule module, final String name) throws ResolutionException {
        this.scope = scope;
        this.module = module;
        this.receiverChainName = receiverName;
        this.environmentHandler = environmentHandler;
        this.name = name;

        taskQueue = IOC.resolve(Keys.getKeyByName("task_queue"));
    }

    /**
     * Parse a message from the given request.
     * Endpoint can receive message in different formats, so we can't make this process common for now.
     *
     * @param request request to the endpoint
     * @param ctx     context of the request
     * @return a deserialized message
     * @throws Exception if there is exception at environment getting
     */
    protected abstract IObject getEnvironment(TContext ctx, TRequest request) throws Exception;

    /**
     * Send response to client if there are some problems on handling request
     *
     * @param request         request to the endpoint
     * @param ctx             context of the request
     * @param responseIObject iobject, that will send to client
     * @throws Exception if there is exception at environment getting
     */
    protected abstract void sendExceptionalResponse(TContext ctx, TRequest request, IObject responseIObject)
            throws Exception;

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
            ModuleManager.setCurrentModule(module);
            taskQueue.put(() -> {
                try {
                    IObject environment = getEnvironment(ctx, request);
                    environmentHandler.handle(environment, receiverChainName, null);
                } catch (Exception e) {
                    trySendExceptionalResponse(e, ctx, request);

                    throw new TaskExecutionException(e);
                }
            });
        } catch (Exception e) {
            trySendExceptionalResponse(e, ctx, request);

            throw new ExecutionException(e);
        }
    }

    private void trySendExceptionalResponse(final Exception e, final TContext context, final TRequest request) {
        try {
            sendExceptionalResponse(context, request, IOC.resolve(Keys.getKeyByName("HttpInternalException")));
        } catch (Exception e1) {
            e.addSuppressed(e1);
        }
    }
}
