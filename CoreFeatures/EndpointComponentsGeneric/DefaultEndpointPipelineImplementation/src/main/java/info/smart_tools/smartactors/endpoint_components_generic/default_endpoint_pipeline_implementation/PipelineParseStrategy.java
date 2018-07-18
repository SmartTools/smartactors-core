package info.smart_tools.smartactors.endpoint_components_generic.default_endpoint_pipeline_implementation;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IFunction0;
import info.smart_tools.smartactors.base.interfaces.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.base.interfaces.iresolve_dependency_strategy.exception.ResolveDependencyStrategyException;
import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_pipeline_set.IEndpointPipelineSet;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandler;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Strategy that parses canonical pipeline description and creates a pipeline instance.
 */
public class PipelineParseStrategy implements IResolveDependencyStrategy {
    private final IFieldName stagesFN;

    /**
     * The constructor.
     *
     * @throws ResolutionException if error occurs resolving any dependency
     */
    public PipelineParseStrategy() throws ResolutionException {
        stagesFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "stages");
    }

    private static final int PIPELINE_DESC_ARG_ID = 0;
    private static final int ENDPOINT_CONFIG_ARG_ID = 1;
    private static final int PIPELINE_SET_ARG_ID = 2;

    @Override
    public <T> T resolve(final Object... args) throws ResolveDependencyStrategyException {
        IObject pipelineDesc = (IObject) args[PIPELINE_DESC_ARG_ID];
        IObject endpointConf = (IObject) args[ENDPOINT_CONFIG_ARG_ID];
        IEndpointPipelineSet pipelineSet = (IEndpointPipelineSet) args[PIPELINE_SET_ARG_ID];

        try {
            List stages = (List) pipelineDesc.getValue(stagesFN);
            List<IMessageHandler> handlers = new ArrayList<>(stages.size());

            for (Object stageDesc : stages) {
                Collection<IMessageHandler> stage = IOC.resolve(Keys.getOrAdd("parse endpoint message handler sequence"),
                        stageDesc, endpointConf, pipelineSet);

                handlers.addAll(stage);
            }

            IFunction0 contextFactory = IOC.resolve(Keys.getOrAdd("endpoint message context factory"),
                    pipelineDesc, handlers);

            return (T) new DefaultEndpointPipelineImplementation(handlers, contextFactory);
        } catch (ReadValueException | InvalidArgumentException | ResolutionException e) {
            throw new ResolveDependencyStrategyException(e);
        }
    }
}
