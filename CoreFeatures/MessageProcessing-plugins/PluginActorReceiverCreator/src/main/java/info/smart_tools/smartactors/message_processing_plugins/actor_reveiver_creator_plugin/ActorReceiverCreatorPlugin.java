package info.smart_tools.smartactors.message_processing_plugins.actor_reveiver_creator_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.base.strategy.create_new_instance_strategy.CreateNewInstanceStrategy;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.ioc.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing.actor_receiver_creator.ActorReceiverCreator;
import info.smart_tools.smartactors.message_processing_interfaces.iroutable_object_creator.IRoutedObjectCreator;
import info.smart_tools.smartactors.message_processing_interfaces.iroutable_object_creator.exceptions.ObjectCreationException;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Implementation of {@link IPlugin}.
 * Plugin creates new instance of {@link ActorReceiverCreator} and
 * register its into IOC.
 */
public class ActorReceiverCreatorPlugin  implements IPlugin {

    /** Local storage for instance of {@link IBootstrap}*/
    private IBootstrap<IBootstrapItem<String>> bootstrap;

    /**
     * @param bootstrap Target bootstrap for adding strategy
     * @throws InvalidArgumentException if any errors occurred
     */
    public ActorReceiverCreatorPlugin(final IBootstrap<IBootstrapItem<String>> bootstrap)
            throws InvalidArgumentException {
        if (null == bootstrap) {
            throw new InvalidArgumentException("Incoming argument should not be null.");
        }
        this.bootstrap = bootstrap;
    }

    @Override
    public void load()
            throws PluginException {
        try {
            IBootstrapItem<String> item = new BootstrapItem("ActorReceiverCreator");
            item
                    .after("IOC")
                    .before("starter")
                    .after("InitializeReceiverGenerator")
                    .after("InitializeWrapperGenerator")
                    .after("IFieldPlugin")
                    .after("IFieldNamePlugin")
                    .process(() -> {
                        try {
                            IOC.register(
                                    Keys.getKeyByName("actor_receiver_queue"),
                                    new CreateNewInstanceStrategy(args -> new ConcurrentLinkedQueue()));

                            IOC.register(
                                    Keys.getKeyByName("actor_receiver_busyness_flag"),
                                    new CreateNewInstanceStrategy(args -> new AtomicBoolean(false)));

                            ActorReceiverCreator objectCreator = new ActorReceiverCreator();
                            IOC.register(
                                    Keys.getKeyByName(IRoutedObjectCreator.class.getCanonicalName() + "#actor"),
                                    new SingletonStrategy(objectCreator)
                            );
                        } catch (ResolutionException e) {
                            throw new ActionExecutionException("ActorReceiverCreator plugin can't load: can't get ActorReceiverCreator key", e);
                        } catch (InvalidArgumentException e) {
                            throw new ActionExecutionException("ActorReceiverCreator plugin can't load: can't create strategy", e);
                        } catch (RegistrationException e) {
                            throw new ActionExecutionException("ActorReceiverCreator plugin can't load: can't register new strategy", e);
                        } catch (ObjectCreationException e) {
                            throw new ActionExecutionException("ActorReceiverCreator plugin can't load: constructor error", e);
                        }
                    })
                    .revertProcess(() -> {
                        String[] keyNames = {
                                IRoutedObjectCreator.class.getCanonicalName() + "#actor",
                                "actor_receiver_busyness_flag",
                                "actor_receiver_queue"
                        };
                        Keys.unregisterByNames(keyNames);
                    });
            this.bootstrap.add(item);
        } catch (Throwable e) {
            throw new PluginException("Could not load 'ActorReceiverCreator plugin'", e);
        }
    }
}
