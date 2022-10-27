package info.smart_tools.smartactors.message_processing_plugins.wrapper_creator_receiver_decorator_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.FunctionExecutionException;
import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject_wrapper.IObjectWrapper;
import info.smart_tools.smartactors.ioc.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing.wrapper_creator_receiver_decorator.WrapperCreatorReceiverDecorator;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageReceiver;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

public class WrapperCreatorReceiverDecoratorPlugin extends BootstrapPlugin {
    /**
     * The constructor.
     *
     * @param bootstrap    the bootstrap
     */
    public WrapperCreatorReceiverDecoratorPlugin(final IBootstrap bootstrap) {
            super(bootstrap);
    }

    @Item("wrapper_creator_receiver_decorator_map_strategies")
    @After({"IOC", "IFieldNamePlugin"})
    public void registerMapStrategies()
            throws RegistrationException, ResolutionException, InvalidArgumentException {
        IOC.register(Keys.getKeyByName("wrapper creator receiver decorator non thread safe map"),
                new ApplyFunctionToArgumentsStrategy(args -> new WeakHashMap()));

        IOC.register(Keys.getKeyByName("wrapper creator receiver decorator thread safe map"),
                // TODO:: Use some concurrent map with weak keys; ConcurrentHashMap will cause memory leak when chain containing non-actor \
                // receiver with wrapper configuration is deleted
                new ApplyFunctionToArgumentsStrategy(args -> new ConcurrentHashMap()));
    }

    @ItemRevert("wrapper_creator_receiver_decorator_map_strategies")
    public void unregisterMapStrategies() {
        String[] itemNames = {
                "wrapper creator receiver decorator non thread safe map",
                "wrapper creator receiver decorator thread safe map"
        };
        Keys.unregisterByNames(itemNames);
    }

    @Item("wrapper_creator_receiver_decorator_wrapper_resolution_strategies")
    @After({
        "wds_object",
    })
    public void registerWrapperCreationStrategies()
            throws RegistrationException, ResolutionException, InvalidArgumentException {
        IOC.register(
                Keys.getKeyByName("thread safe environment wrapper creation strategy"),
                new ApplyFunctionToArgumentsStrategy(args -> {
                    try {
                        IObject config = (IObject) args[0];

                        Object conf = IOC.resolve(Keys.getKeyByName("thread safe wrapper configuration"), config);

                        return new ApplyFunctionToArgumentsStrategy(args2 -> {
                            try {
                                IObjectWrapper wrapper = IOC.resolve(
                                        Keys.getKeyByName("info.smart_tools.smartactors.iobject_extension.wds_object.WDSObject"),
                                        conf
                                );

                                wrapper.init((IObject) args2[0]);

                                return wrapper;
                            } catch (ClassCastException | ResolutionException e) {
                                throw new FunctionExecutionException(e);
                            }
                        });
                    } catch (ClassCastException | ResolutionException e) {
                        throw new FunctionExecutionException(e);
                    }
                })
        );

        IOC.register(
                Keys.getKeyByName("non thread safe environment wrapper creation strategy"),
                new ApplyFunctionToArgumentsStrategy(args -> {
                    try {
                        IObject config = (IObject) args[0];

                        Object conf = IOC.resolve(Keys.getKeyByName("non thread safe wrapper configuration"), config);

                        IObjectWrapper wrapper = IOC.resolve(
                                Keys.getKeyByName("info.smart_tools.smartactors.iobject_extension.wds_object.WDSObject"),
                                conf
                        );

                        return new ApplyFunctionToArgumentsStrategy(args2 -> {
                            wrapper.init((IObject) args2[0]);
                            return wrapper;
                        });
                    } catch (ClassCastException | ResolutionException e) {
                        throw new FunctionExecutionException(e);
                    }
                })
        );
    }

    @ItemRevert("wrapper_creator_receiver_decorator_wrapper_resolution_strategies")
    public void unregisterWrapperCreationStrategies() {
        String[] itemNames = {
                "thread safe environment wrapper creation strategy",
                "non thread safe environment wrapper creation strategy"
        };
        Keys.unregisterByNames(itemNames);
    }

    @Item("wrapper_creator_receiver_decorator")
    @After({
        "wrapper_creator_receiver_decorator_map_strategies",
        "wrapper_creator_receiver_decorator_wrapper_resolution_strategies",
    })
    public void registerDecoratorCreationStrategy()
            throws RegistrationException, ResolutionException, InvalidArgumentException {
        IOC.register(
                Keys.getKeyByName("thread safe wrapper creator receiver decorator"),
                wrapperCreatorDecoratorStrategy(
                        "wrapper creator receiver decorator thread safe map",
                        "thread safe environment wrapper creation strategy"
                ));

        IOC.register(
                Keys.getKeyByName("non thread safe wrapper creator receiver decorator"),
                wrapperCreatorDecoratorStrategy(
                        "wrapper creator receiver decorator non thread safe map",
                        "non thread safe environment wrapper creation strategy"
                ));
    }

    @ItemRevert("wrapper_creator_receiver_decorator")
    public void unregisterDecoratorCreationStrategy() {
        String[] itemNames = {
                "thread safe wrapper creator receiver decorator",
                "non thread safe wrapper creator receiver decorator"
        };
        Keys.unregisterByNames(itemNames);
    }

    private static IStrategy wrapperCreatorDecoratorStrategy(
        final String mapDependency, final String wrapperStrategyDependency)
            throws InvalidArgumentException {
        return new ApplyFunctionToArgumentsStrategy(args -> {
            try {
                IMessageReceiver underlying = (IMessageReceiver) args[0];

                Map<Object, IStrategy> strategyMap = IOC.resolve(Keys.getKeyByName(mapDependency));

                return new WrapperCreatorReceiverDecorator(underlying, strategyMap, wrapperStrategyDependency);
            } catch (ClassCastException | ResolutionException e) {
                throw new FunctionExecutionException(e);
            }
        });
    }
}
