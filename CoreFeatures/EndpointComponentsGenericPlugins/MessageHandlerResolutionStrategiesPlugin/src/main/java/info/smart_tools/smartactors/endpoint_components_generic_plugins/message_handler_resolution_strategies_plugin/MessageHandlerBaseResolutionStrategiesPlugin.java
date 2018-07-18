package info.smart_tools.smartactors.endpoint_components_generic_plugins.message_handler_resolution_strategies_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.i_addition_dependency_strategy.IAdditionDependencyStrategy;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.FunctionExecutionException;
import info.smart_tools.smartactors.base.interfaces.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.base.interfaces.iresolve_dependency_strategy.exception.ResolveDependencyStrategyException;
import info.smart_tools.smartactors.base.simple_strict_storage_strategy.SimpleStrictStorageStrategy;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.base.strategy.strategy_storage_strategy.StrategyStorageStrategy;
import info.smart_tools.smartactors.endpoint_components_generic.handler_configuration_object.HandlerConfigurationObject;
import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_pipeline_set.IEndpointPipelineSet;
import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_pipeline_set.exceptions.PipelineCreationException;
import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_pipeline_set.exceptions.PipelineDescriptionNotFoundException;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;

import java.util.Collections;
import java.util.Map;

public class MessageHandlerBaseResolutionStrategiesPlugin extends BootstrapPlugin {
    /**
     * The constructor.
     *
     * @param bootstrap    the bootstrap
     */
    public MessageHandlerBaseResolutionStrategiesPlugin(final IBootstrap bootstrap) {
            super(bootstrap);
    }

    @Item("base_message_handler_strategies")
    @After({
            "endpoint_pipeline_handler_configuration_object_strategy",
    })
    public void registerBaseStrategies() throws Exception {
        SimpleStrictStorageStrategy messageHandlerStrategyStorage
                = new SimpleStrictStorageStrategy("endpoint message handler");

        /*
         * (String type, IObject handlerConf, IObject endpointConf, IEndpointPipelineSet pipelineSet)
         *      -> IMessageHandler
         */
        IOC.register(Keys.getOrAdd("endpoint message handler"), new ApplyFunctionToArgumentsStrategy(args -> {
            try {
                return messageHandlerStrategyStorage.resolve(
                        args[0],
                        IOC.resolve(Keys.getOrAdd("endpoint message handler configuration object"), args[1], args[2]),
                        args[2],
                        args[3]
                );
            } catch (ResolveDependencyStrategyException | ResolutionException e) {
                throw new FunctionExecutionException(e);
            }
        }));
        IOC.register(Keys.getOrAdd("expandable_strategy#endpoint message handler"),
                new SingletonStrategy(messageHandlerStrategyStorage));

        StrategyStorageStrategy messageHandlerSequenceStrategyStorage = new StrategyStorageStrategy(
                x -> x,
                (map, key) -> {
                    Map m = (Map) map;

                    IResolveDependencyStrategy strategy = (IResolveDependencyStrategy) m.get(key);

                    if (null == strategy) {
                        strategy = new IResolveDependencyStrategy() {
                            @Override
                            public <T> T resolve(final Object... args) throws ResolveDependencyStrategyException {
                                try {
                                    return (T) Collections.singletonList(
                                            IOC.resolve(Keys.getOrAdd("endpoint message handler"), args));
                                } catch (ResolutionException e) {
                                    throw new ResolveDependencyStrategyException(e);
                                }
                            }
                        };
                    }

                    return strategy;
                }
        );

        /*
         * (String type, IObject handlerConf, IObject endpointConf, IEndpointPipelineSet pipelineSet)
         *      -> Iterable<IMessageHandler>
         */
        IOC.register(Keys.getOrAdd("endpoint message handler sequence"), messageHandlerSequenceStrategyStorage);
        IOC.register(Keys.getOrAdd("expandable_strategy#endpoint message handler sequence"),
                new SingletonStrategy(messageHandlerSequenceStrategyStorage));

        IFieldName typeFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "type");

        /*
         * (IObject handlerConf, IObject endpointConf, IEndpointPipelineSet pipelineSet)
         *      -> IMessageHandler
         */
        IOC.register(Keys.getOrAdd("parse endpoint message handler"),
                new ApplyFunctionToArgumentsStrategy(args -> {
            try {
                Object type = ((IObject) args[0]).getValue(typeFN);
                return IOC.resolve(Keys.getOrAdd("endpoint message handler"), type, args[0], args[1], args[2]);
            } catch (ReadValueException | InvalidArgumentException | ResolutionException e) {
                throw new FunctionExecutionException(e);
            }
        }));

        /*
         * (IObject handlerConf, IObject endpointConf, IEndpointPipelineSet pipelineSet)
         *      -> Iterable<IMessageHandler>
         */
        IOC.register(Keys.getOrAdd("parse endpoint message handler sequence"),
                new ApplyFunctionToArgumentsStrategy(args -> {
            try {
                Object type = ((IObject) args[0]).getValue(typeFN);
                return IOC.resolve(Keys.getOrAdd("endpoint message handler sequence"),
                        type, args[0], args[1], args[2]);
            } catch (ReadValueException | InvalidArgumentException | ResolutionException e) {
                throw new FunctionExecutionException(e);
            }
        }));
    }

    /**
     * Register strategy that processes {@code "include"} steps of pipeline configuration.
     *
     * <pre>
     *  {
     *      "type": "include",
     *      "pipeline": ".. pipeline id .."
     *  }
     * </pre>
     *
     * @throws Exception if any error occurs
     */
    @Item("pipeline_include_handler_sequence_strategy")
    @After({
            "base_message_handler_strategies"
    })
    public void registerIncludeStrategy() throws Exception {
        IFieldName pipelineFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "pipeline");

        IAdditionDependencyStrategy seqStrategy = IOC.resolve(
                Keys.getOrAdd("expandable_strategy#endpoint message handler sequence"));

        seqStrategy.register("include", new ApplyFunctionToArgumentsStrategy(args -> {
                IObject handlerConf = (IObject) args[1];
                IEndpointPipelineSet pipelineSet = (IEndpointPipelineSet) args[3];

                try {
                    return pipelineSet.getPipeline((String) handlerConf.getValue(pipelineFN)).getHandlers();
                } catch (ReadValueException | InvalidArgumentException | PipelineDescriptionNotFoundException
                            | PipelineCreationException e) {
                    throw new FunctionExecutionException(e);
                }
            }));
    }

    @Item("endpoint_pipeline_handler_configuration_object_strategy")
    public void registerHandlerConfigObjectStrategy() throws Exception {
        IOC.register(Keys.getOrAdd("endpoint message handler configuration object"),
                new ApplyFunctionToArgumentsStrategy(args -> {
            IObject[] objects = new IObject[args.length];

            for (int i = 0; i < args.length; i++) {
                objects[i] = (IObject) args[i];
            }

            return new HandlerConfigurationObject(objects);
        }));
    }
}
