package info.smart_tools.smartactors.endpoint_components_generic.message_handler_resolution_strategy;

import info.smart_tools.smartactors.base.interfaces.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.base.interfaces.iresolve_dependency_strategy.exception.ResolveDependencyStrategyException;
import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_pipeline_set.IEndpointPipelineSet;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandler;
import info.smart_tools.smartactors.iobject.iobject.IObject;

/**
 * Convenience class for strategies resolving {@link IMessageHandler message handler} instances.
 */
public class MessageHandlerResolutionStrategy implements IResolveDependencyStrategy {
    private static final int ARG_IDX_TYPE = 0;
    private static final int ARG_IDX_HANDLER_CONF = 1;
    private static final int ARG_IDX_ENDPOINT_CONF = 2;
    private static final int ARG_IDX_PIPELINE_SET = 3;

    /**
     * Interface for a function resolving {@link IMessageHandler message handlers}.
     */
    @FunctionalInterface public interface Function {

        /**
         * @param type         handler type name
         * @param handlerConf  handler configuration object
         * @param endpointConf endpoint configuration object
         * @param pipelineSet  pipeline set of endpoint
         * @return resolved message handler
         * @throws Exception if any error occurs
         */
        IMessageHandler resolve(String type, IObject handlerConf, IObject endpointConf, IEndpointPipelineSet pipelineSet)
                throws Exception;
    }

    /**
     * The constructor.
     *
     * @param function function resolving a handler (probably a lambda expression)
     */
    public MessageHandlerResolutionStrategy(final Function function) {
        this.function = function;
    }

    private final Function function;

    @Override
    public <T> T resolve(final Object... args) throws ResolveDependencyStrategyException {
        String type;
        IObject handlerConf, endpointConf;
        IEndpointPipelineSet pipelineSet;

        try {
            type = (String) args[ARG_IDX_TYPE];
            handlerConf = (IObject) args[ARG_IDX_HANDLER_CONF];
            endpointConf = (IObject) args[ARG_IDX_ENDPOINT_CONF];
            pipelineSet = (IEndpointPipelineSet) args[ARG_IDX_PIPELINE_SET];
        } catch (ClassCastException e) {
            throw new ResolveDependencyStrategyException("Invalid argument type.", e);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ResolveDependencyStrategyException("NotEnough arguments.");
        }

        try {
            return (T) function.resolve(type, handlerConf, endpointConf, pipelineSet);
        } catch (Exception e) {
            throw new ResolveDependencyStrategyException(e);
        }
    }
}
