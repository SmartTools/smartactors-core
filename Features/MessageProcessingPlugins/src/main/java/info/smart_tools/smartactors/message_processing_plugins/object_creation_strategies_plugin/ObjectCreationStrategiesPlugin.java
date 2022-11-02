package info.smart_tools.smartactors.message_processing_plugins.object_creation_strategies_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.FunctionExecutionException;
import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.ioc.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing.actor_receiver.ActorReceiver;
import info.smart_tools.smartactors.message_processing.handler_routing_receiver.HandlerRoutingReceiver;
import info.smart_tools.smartactors.message_processing.object_creation_strategies.FullObjectCreatorStrategy;
import info.smart_tools.smartactors.message_processing.object_creation_strategies.MethodInvokerReceiverStrategy;
import info.smart_tools.smartactors.message_processing.object_creation_strategies.RouterRegistrationObjectListener;
import info.smart_tools.smartactors.message_processing_interfaces.iwrapper_generator.IWrapperGenerator;
import info.smart_tools.smartactors.message_processing_interfaces.iwrapper_generator.exception.WrapperGeneratorException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageReceiver;

import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class ObjectCreationStrategiesPlugin extends BootstrapPlugin {
    /**
     * The constructor.
     *
     * @param bootstrap    the bootstrap
     */
    public ObjectCreationStrategiesPlugin(final IBootstrap bootstrap) {
            super(bootstrap);
    }

    @Item("global_router_registration_receiver_object_listener")
    @After({
        "router",
        "IOC",
    })
    @Before({
            "object_creation_strategies:done",
    })
    public void registerRouterListener()
            throws ResolutionException, RegistrationException, InvalidArgumentException {
        IOC.register(
                Keys.getKeyByName("global router registration receiver object listener"),
                new SingletonStrategy(new RouterRegistrationObjectListener())
        );
    }

    @ItemRevert("global_router_registration_receiver_object_listener")
    public void unregisterRouterListener() {
        String[] itemNames = { "global router registration receiver object listener" };
        Keys.unregisterByNames(itemNames);
    }

    @Item("full_object_creator_strategy")
    @After({
            "IOC",
    })
    @Before({
            "object_creation_strategies:done",
    })
    public void registerFullCreatorStrategy()
            throws ResolutionException, RegistrationException, InvalidArgumentException {
        IOC.register(
                Keys.getKeyByName("full receiver object creator"),
                new FullObjectCreatorStrategy()
        );
    }

    @ItemRevert("full_object_creator_strategy")
    public void unregisterFullCreatorStrategy() {
        String[] itemNames = { "full receiver object creator" };
        Keys.unregisterByNames(itemNames);
    }

    @Item("basic_receiver_strategies")
    @After({
        "IOC",
    })
    public void registerBasicReceiver()
            throws ResolutionException, RegistrationException, InvalidArgumentException {
        // Dependencies of ActorReceiver
        IOC.register(
                Keys.getKeyByName("actor_receiver_queue"),
                new ApplyFunctionToArgumentsStrategy(args -> new ConcurrentLinkedQueue()));
        IOC.register(
                Keys.getKeyByName("actor_receiver_busyness_flag"),
                new ApplyFunctionToArgumentsStrategy(args -> new AtomicBoolean(false)));

        IOC.register(
                Keys.getKeyByName("create actor synchronization receiver"),
                new ApplyFunctionToArgumentsStrategy(args -> {
                    try {
                        return new ActorReceiver((IMessageReceiver) args[0]);
                    } catch (InvalidArgumentException | ResolutionException e) {
                        throw new FunctionExecutionException(e);
                    }
                })
        );

        IOC.register(
                Keys.getKeyByName("create handler router receiver"),
                new ApplyFunctionToArgumentsStrategy(args -> {
                    try {
                        return new HandlerRoutingReceiver((Map) args[0]);
                    } catch (InvalidArgumentException | ResolutionException e) {
                        throw new FunctionExecutionException(e);
                    }
                })
        );
    }

    @ItemRevert("basic_receiver_strategies")
    public void unregisterBasicReceiver() {
        String[] itemNames = {
                "actor_receiver_queue",
                "actor_receiver_busyness_flag",
                "create actor synchronization receiver",
                "create handler router receiver"
        };
        Keys.unregisterByNames(itemNames);
    }

    @Item("wrapper_resolution_strategies_for_invokers")
    @After({
            "IOC",
    })
    public void registerWrapperResolutionStrategies()
            throws ResolutionException, RegistrationException, InvalidArgumentException {
        IStrategy newInstanceWrapperStrategy = new ApplyFunctionToArgumentsStrategy(args -> {
            Class<?> clazz = (Class) args[0];

            try {
                IWrapperGenerator wrapperGenerator = IOC.resolve(Keys.getKeyByName(IWrapperGenerator.class.getCanonicalName()));

                Object wrapper = wrapperGenerator.generate(clazz);

                return new ApplyFunctionToArgumentsStrategy(args1 -> {
                    try {
                        return wrapper.getClass().newInstance();
                    } catch (Exception e) {
                        throw new FunctionExecutionException(e);
                    }
                });
            } catch (ResolutionException | WrapperGeneratorException e) {
                throw new FunctionExecutionException(e);
            }
        });

        IStrategy singletonWrapperStrategy = new ApplyFunctionToArgumentsStrategy(args -> {
            Class<?> clazz = (Class) args[0];

            try {
                IWrapperGenerator wrapperGenerator = IOC.resolve(Keys.getKeyByName(IWrapperGenerator.class.getCanonicalName()));

                Object wrapper = wrapperGenerator.generate(clazz);

                return new SingletonStrategy(wrapper);
            } catch (ResolutionException | WrapperGeneratorException e) {
                throw new FunctionExecutionException(e);
            }
        });

        IOC.register(
                Keys.getKeyByName("default wrapper resolution strategy dependency for invoker receiver"),
                newInstanceWrapperStrategy
        );

        IOC.register(
                Keys.getKeyByName("new instance wrapper resolution strategy"),
                newInstanceWrapperStrategy
        );

        IOC.register(
                Keys.getKeyByName("singleton wrapper resolution strategy"),
                singletonWrapperStrategy
        );
    }

    @ItemRevert("wrapper_resolution_strategies_for_invokers")
    public void unregisterWrapperResolutionStrategies() {
        String[] itemNames = {
                "singleton wrapper resolution strategy",
                "new instance wrapper resolution strategy",
                "default wrapper resolution strategy dependency for invoker receiver"
        };
        Keys.unregisterByNames(itemNames);
    }

    @Item("invoker_receiver_creation_strategy")
    @After({
        "wrapper_resolution_strategies_for_invokers",
        "IFieldNamePlugin",
        "InitializeReceiverGenerator",
    })
    @Before({
        "object_creation_strategies:done",
    })
    public void registerInvokerCreationStrategy()
            throws ResolutionException, RegistrationException, InvalidArgumentException {
        IOC.register(
                Keys.getKeyByName("method invoker receiver"),
                new MethodInvokerReceiverStrategy()
        );
    }

    @ItemRevert("invoker_receiver_creation_strategy")
    public void unregisterInvokerCreationStrategy() {
        String[] itemNames = { "method invoker receiver" };
        Keys.unregisterByNames(itemNames);
    }

    @Item("object_creation_strategies:done")
    public void creationStrategiesDone() {
    }

    @ItemRevert("object_creation_strategies:done")
    public void destructionStrategiesDone() {
    }
}
