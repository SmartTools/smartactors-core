package info.smart_tools.smartactors.core.endpoint_handler;

import info.smart_tools.smartactors.core.ienvironment_extractor.IEnvironmentExtractor;
import info.smart_tools.smartactors.core.ienvironment_extractor.exceptions.EnvironmentExtractionException;
import info.smart_tools.smartactors.core.ienvironment_handler.IEnvironmentHandler;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.itask.ITask;
import info.smart_tools.smartactors.core.itask.exception.TaskExecutionException;
import info.smart_tools.smartactors.core.message_processing.IReceiverChain;

/**
 * {@link ITask} implementation for endpoint, that should deserialize request and form
 * {@link info.smart_tools.smartactors.core.message_processor.MessageProcessor}
 */
public class EndpointHandlerTask<TContext, TRequest> implements ITask {
    private final Object context;
    private final Object request;
    private final IEnvironmentExtractor environmentExtractor;
    private final IEnvironmentHandler environmentHandler;
    private final IReceiverChain receiverChain;

    /**
     * Constructor
     *
     * @param environmentExtractor extractor of the environment
     * @param context              context of the request
     * @param request              request
     * @param environmentHandler   handler for environment
     * @param receiverChain        chain that receive message from current endpoint
     */
    public EndpointHandlerTask(final IEnvironmentExtractor environmentExtractor, final Object context,
                               final Object request, final IEnvironmentHandler environmentHandler,
                               final IReceiverChain receiverChain) {
        this.environmentExtractor = environmentExtractor;
        this.context = context;
        this.request = request;
        this.environmentHandler = environmentHandler;
        this.receiverChain = receiverChain;
    }

    @Override
    public void execute() throws TaskExecutionException {
        IObject environment = null;
        try {
            environment = environmentExtractor.extract(request, context);
        } catch (EnvironmentExtractionException e) {
            throw new TaskExecutionException("Failed to extract environment", e);
        }
        environmentHandler.handle(environment, receiverChain);
    }
}
