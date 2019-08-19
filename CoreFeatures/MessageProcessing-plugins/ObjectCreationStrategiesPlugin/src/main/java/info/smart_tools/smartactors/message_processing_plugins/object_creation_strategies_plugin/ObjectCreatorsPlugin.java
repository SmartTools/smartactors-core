package info.smart_tools.smartactors.message_processing_plugins.object_creation_strategies_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.FunctionExecutionException;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing.object_creation_strategies.GenericDecoratorReceiverObjectCreator;
import info.smart_tools.smartactors.message_processing.object_creation_strategies.HandlerRouterReceiverCreator;
import info.smart_tools.smartactors.message_processing.object_creation_strategies.PerReceiverActorSynchronizationReceiverCreator;
import info.smart_tools.smartactors.message_processing.object_creation_strategies.SetAddressFromObjectNameReceiverCreator;
import info.smart_tools.smartactors.message_processing.object_creation_strategies.TopLevelObjectCreator;
import info.smart_tools.smartactors.message_processing.object_creation_strategies.UserObjectMethodInvokerReceiverCreator;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.IReceiverObjectCreator;

import java.util.Arrays;

/**
 * Registers strategies for creation of {@link IReceiverObjectCreator object creators} and default configuration objects for named filter
 * types.
 */
public class ObjectCreatorsPlugin extends BootstrapPlugin {
    /**
     * The constructor.
     *
     * @param bootstrap the bootstrap
     */
    public ObjectCreatorsPlugin(final IBootstrap bootstrap) {
        super(bootstrap);
    }

    @FunctionalInterface
    interface CreatorConstructor {
        IReceiverObjectCreator call(IReceiverObjectCreator underlyingObjectCreator, IObject filterConfig, IObject objectConfig)
                throws ResolutionException;
    }

    private void registerCreatorType(final String typeName, final CreatorConstructor constructor)
            throws ResolutionException, RegistrationException, InvalidArgumentException, ChangeValueException {
        registerCreatorType(typeName, constructor, IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject")));
    }

    private void registerCreatorType(final String typeName, final CreatorConstructor constructor, final IObject namedFilterConfig)
            throws ResolutionException, RegistrationException, InvalidArgumentException, ChangeValueException {
        String dependencyName = "filter creator#" + typeName;
        IOC.register(
                Keys.getKeyByName(dependencyName),
                new ApplyFunctionToArgumentsStrategy(args -> {
                    try {
                        IReceiverObjectCreator underlyingCreator = (IReceiverObjectCreator) args[0];
                        IObject filterConf = (IObject) args[1];
                        IObject objectConf = (IObject) args[2];

                        return constructor.call(underlyingCreator, filterConf, objectConf);
                    } catch (ResolutionException | ClassCastException e) {
                        throw new FunctionExecutionException(e);
                    }
                })
        );

        if (null != namedFilterConfig) {
            namedFilterConfig.setValue(
                    IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "dependency"),
                    dependencyName
            );

            IOC.register(
                    Keys.getKeyByName("named filter config#" + typeName),
                    new SingletonStrategy(namedFilterConfig)
            );
        }
    }

    private void unregisterCreatorType(final String typeName) {
        try {
            unregisterCreatorType(typeName, IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject")));
        } catch (ResolutionException e) {
            // TODO: Empty catch block
        }
    }

    private void unregisterCreatorType(final String typeName, final IObject namedFilterConfig) {
        String[] keyNames = { "filter creator#" + typeName };
        Keys.unregisterByNames(keyNames);

        if (null != namedFilterConfig) {
            /*
            String keyName = "info.smart_tools.smartactors.iobject.ifield_name.IFieldName";
            try {
                namedFilterConfig.deleteField(IOC.resolve(Keys.getKeyByName(keyName), "dependency"));
            } catch(InvalidArgumentException | DeleteValueException e) {
                System.out.println("[WARNING] Field '"+keyName+"' deletion failed");
            } catch (ResolutionException e) { }
            */
            keyNames[0] = "named filter config#" + typeName;
            Keys.unregisterByNames(keyNames);
        }
    }

    @Item("basic_object_creators")
    @After({
            "basic_receiver_strategies",            // for HandlerRouterReceiverCreator and PerReceiverActorSynchronizationReceiverCreator
            "invoker_receiver_creation_strategy",   // required for UserObjectMethodInvokerReceiverCreator
            "iobject",
            "IFieldNamePlugin",
            "wrapper_creator_receiver_decorator",
    })
    @Before({
            "object_creation_strategies:done",
    })
    public void registerCreators()
            throws ResolutionException, RegistrationException, InvalidArgumentException, ChangeValueException {
        IReceiverObjectCreator tlCreator = new TopLevelObjectCreator();
        registerCreatorType(
                "top-level object",
                (b, a, r) -> tlCreator);

        registerCreatorType(
                "method invokers",
                UserObjectMethodInvokerReceiverCreator::new);
        registerCreatorType(
                "handler router receiver",
                HandlerRouterReceiverCreator::new);
        registerCreatorType(
                "per-receiver actor sync",
                PerReceiverActorSynchronizationReceiverCreator::new);
        registerCreatorType(
                "set address from name",
                SetAddressFromObjectNameReceiverCreator::new);
        registerCreatorType(
                "decorate receiver",
                GenericDecoratorReceiverObjectCreator::new,
                null);

        IObject tsWrapperCreatorConfig = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"));
        tsWrapperCreatorConfig.setValue(
                IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "dependency"),
                "filter creator#decorate receiver");
        tsWrapperCreatorConfig.setValue(
                IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "decoratorDependency"),
                "thread safe wrapper creator receiver decorator");

        IOC.register(
                Keys.getKeyByName("named filter config#thread-safe wrapper creator"),
                new SingletonStrategy(tsWrapperCreatorConfig)
        );

        IObject ntsWrapperCreatorConfig = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"));
        ntsWrapperCreatorConfig.setValue(
                IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "dependency"),
                "filter creator#decorate receiver");
        ntsWrapperCreatorConfig.setValue(
                IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "decoratorDependency"),
                "non thread safe wrapper creator receiver decorator");
        IOC.register(
                Keys.getKeyByName("named filter config#non-thread-safe wrapper creator"),
                new SingletonStrategy(ntsWrapperCreatorConfig)
        );
    }

    @ItemRevert("basic_object_creators")
    public void unregisterCreators() {
        String[] keyNames = {
                "named filter config#non-thread-safe wrapper creator",
                "named filter config#thread-safe wrapper creator"
        };
        Keys.unregisterByNames(keyNames);

        unregisterCreatorType("decorate receiver", null);
        unregisterCreatorType("set address from name");
        unregisterCreatorType("per-receiver actor sync");
        unregisterCreatorType("handler router receiver");
        unregisterCreatorType("method invokers");
        unregisterCreatorType("top-level object");
    }

    @Item("basic_object_kinds")
    @After({
        "IOC",
    })
    @Before({
            "object_creation_strategies:done",
    })
    public void registerKinds()
            throws RegistrationException, ResolutionException, InvalidArgumentException {
        IOC.register(
                Keys.getKeyByName("object kind filter sequence#raw"),
                new SingletonStrategy(Arrays.asList(
                        "top-level object",
                        "thread-safe wrapper creator",
                        "set address from name"
                ))
        );
        IOC.register(
                Keys.getKeyByName("object kind filter sequence#stateless_actor"),
                new SingletonStrategy(Arrays.asList(
                        "top-level object",
                        "method invokers",
                        "handler router receiver",
                        "thread-safe wrapper creator",
                        "set address from name"
                ))
        );
        IOC.register(
                Keys.getKeyByName("object kind filter sequence#actor"),
                new SingletonStrategy(Arrays.asList(
                        "top-level object",
                        "method invokers",
                        "handler router receiver",
                        "non-thread-safe wrapper creator",
                        "per-receiver actor sync",
                        "set address from name"
                ))
        );
    }

    @ItemRevert("basic_object_kinds")
    public void unregisterKinds() {
        String[] keyNames = {
                "object kind filter sequence#raw",
                "object kind filter sequence#stateless_actor",
                "object kind filter sequence#actor"
        };
        Keys.unregisterByNames(keyNames);
    }
}
