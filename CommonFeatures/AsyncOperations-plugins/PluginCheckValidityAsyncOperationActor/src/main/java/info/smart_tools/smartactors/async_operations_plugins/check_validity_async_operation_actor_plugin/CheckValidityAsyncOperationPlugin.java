package info.smart_tools.smartactors.async_operations_plugins.check_validity_async_operation_actor_plugin;

import info.smart_tools.smartactors.async_operations.check_validity_async_operation.CheckValidityAsyncOperationActor;
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

/**
 * Plugin for load IOC-strategy for check validity asynchronous operation actor
 */
public class CheckValidityAsyncOperationPlugin implements IPlugin {

    private final IBootstrap<IBootstrapItem<String>> bootstrap;

    /**
     * Constructor for create instance of CheckValidityAsyncOperationPlugin
     * @param bootstrap the bootstrap
     */
    public CheckValidityAsyncOperationPlugin(final IBootstrap<IBootstrapItem<String>> bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    public void load() throws PluginException {
        try {
            IBootstrapItem<String> item = new BootstrapItem("CreateCheckValidityAsyncOperationActor");

            item
//                    .after("IOC")
//                    .before("starter")
                    .process(() -> {
                try {
                    IKey operationKey = Keys.getKeyByName(CheckValidityAsyncOperationActor.class.getCanonicalName());
                    IOC.register(operationKey, new ApplyFunctionToArgumentsStrategy(
                            (args) -> {
                                return new CheckValidityAsyncOperationActor((IObject) args[0]);
                        }
                    ));
                } catch (ResolutionException e) {
                    throw new ActionExecutionException("CheckValidityAsyncOperationActor plugin can't load: can't get CheckValidityAsyncOperationActor key", e);
                } catch (InvalidArgumentException e) {
                    throw new ActionExecutionException("CheckValidityAsyncOperationActor plugin can't load: can't create strategy", e);
                } catch (RegistrationException e) {
                    throw new ActionExecutionException("CheckValidityAsyncOperationActor plugin can't load: can't register new strategy", e);
                }
            });
            bootstrap.add(item);
        } catch (InvalidArgumentException e) {
            throw new PluginException("Can't load CheckValidityAsyncOperation plugin", e);
        }
    }
}