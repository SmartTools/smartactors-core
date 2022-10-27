package info.smart_tools.smartactors.async_operations_plugins.get_async_operation_plugin;

import info.smart_tools.smartactors.async_operations.get_async_operation.GetAsyncOperationActor;
import info.smart_tools.smartactors.async_operations.get_async_operation.exception.GetAsyncOperationActorException;
import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
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
 * Plugin for {@link GetAsyncOperationActor}
 */
public class GetAsyncOperationPlugin implements IPlugin {

    private IBootstrap<IBootstrapItem<String>> bootstrap;

    /**
     * Constructor for GetAsyncOperationPlugin
     * @param bootstrap the bootstrap
     */
    public GetAsyncOperationPlugin(final IBootstrap<IBootstrapItem<String>> bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    public void load() throws PluginException {
        try {
            IBootstrapItem<String> item = new BootstrapItem("CreateGetAsyncOperationActor");

            item
//                    .after("IOC")
//                    .before("starter")
                    .process(() -> {
                        try {
                            IKey actorKey = Keys.getKeyByName(GetAsyncOperationActor.class.getCanonicalName());
                            IOC.register(actorKey, new ApplyFunctionToArgumentsStrategy(
                                    (args) -> {
                                        try {
                                            return new GetAsyncOperationActor((IObject) args[0]);
                                        } catch (GetAsyncOperationActorException e) {
                                            throw new RuntimeException("Can't create GetAsyncOperationActor with params: " + Arrays.toString(args), e);
                                        }
                                    }
                            ));
                        } catch (ResolutionException e) {
                            throw new ActionExecutionException("GetAsyncOperationActor plugin can't load: can't get GetAsyncOperationActor key", e);
                        } catch (InvalidArgumentException e) {
                            throw new ActionExecutionException("GetAsyncOperationActor plugin can't load: can't create strategy", e);
                        } catch (RegistrationException e) {
                            throw new ActionExecutionException("GetAsyncOperationActor plugin can't load: can't register new strategy", e);
                        }
                    });
            bootstrap.add(item);
        } catch (Exception e) {
            throw new PluginException("Can't load GetAsyncOperationPlugin", e);
        }

    }
}
