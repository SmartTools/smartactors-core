package info.smart_tools.smartactors.async_operations_plugins.close_async_operation_actor_plugin;

import info.smart_tools.smartactors.async_operations.close_async_operation.CloseAsyncOperationActor;
import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.base.strategy.create_new_instance_strategy.CreateNewInstanceStrategy;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;

import java.util.Arrays;
/**
 * Plugin for implement @CloseAsyncOperationActor
 */
public class CloseAsyncOperationActorPlugin implements IPlugin {

    private final IBootstrap<IBootstrapItem<String>> bootstrap;

    /**
     * Constructor
     * @param bootstrap bootstrap element
     */
    public CloseAsyncOperationActorPlugin(final IBootstrap<IBootstrapItem<String>> bootstrap) {
        this.bootstrap = bootstrap;
    }

    /**
     * Loading plugin
     * @throws PluginException throw when plugin can't be loaded
     */
    @Override
    public void load() throws PluginException {
        try {
            IBootstrapItem<String> item = new BootstrapItem("CloseAsyncOperationActorPlugin");

            item
//                    .after("IOC")
//                    .before("starter")
                    .process(() -> {
                        try {
                            IKey checkUserByEmailActorKey = Keys.getKeyByName(CloseAsyncOperationActor.class.getCanonicalName());
                            IOC.register(checkUserByEmailActorKey,
                                    new CreateNewInstanceStrategy(
                                            (args) -> {
                                                try {
                                                    return new CloseAsyncOperationActor((IObject) args[0]);
                                                } catch (ArrayIndexOutOfBoundsException e) {
                                                    throw new RuntimeException(
                                                            "Can't get args: args must contain one or more elements " +
                                                                    "and first element must be IObject",
                                                            e);
                                                } catch (InvalidArgumentException e) {
                                                    throw new RuntimeException(
                                                            "Can't create actor with this args: "
                                                                    + Arrays.toString(args),
                                                            e);
                                                }
                                            }
                                    )
                            );
                        } catch (ResolutionException e) {
                            throw new ActionExecutionException("CloseAsyncOperationActor plugin can't load: can't get CloseAsyncOperationActor key", e);
                        } catch (InvalidArgumentException e) {
                            throw new ActionExecutionException("CloseAsyncOperationActor plugin can't load: can't create strategy", e);
                        } catch (RegistrationException e) {
                            throw new ActionExecutionException("CloseAsyncOperationActor plugin can't load: can't register new strategy", e);
                        }
                    });
            bootstrap.add(item);
        } catch (InvalidArgumentException e) {
            throw new PluginException("Can't get BootstrapItem from one of reason", e);
        }
    }
    
}
