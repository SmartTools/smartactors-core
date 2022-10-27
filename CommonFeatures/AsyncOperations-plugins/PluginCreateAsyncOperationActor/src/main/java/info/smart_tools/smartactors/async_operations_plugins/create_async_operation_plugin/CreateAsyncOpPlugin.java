package info.smart_tools.smartactors.async_operations_plugins.create_async_operation_plugin;

import info.smart_tools.smartactors.async_operations.create_async_operation.CreateAsyncOperationActor;
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

/**
 * Plugin that load actor for creating async operations
 */
public class CreateAsyncOpPlugin implements IPlugin {
    private final IBootstrap<IBootstrapItem<String>> bootstrap;

    /**
     * Constructor
     * @param bootstrap the bootstrap
     */
    public CreateAsyncOpPlugin(final IBootstrap<IBootstrapItem<String>> bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    public void load() throws PluginException {
        try {
            IBootstrapItem<String> item = new BootstrapItem("CreateAsyncOperationActorPlugin");

            item
//                    .after("IOC")
//                    .after("datetime_formatter_plugin")
//                    .before("starter")
                    .process(() -> {
                        try {
                            IKey createAsyncOpKey = Keys.getKeyByName(CreateAsyncOperationActor.class.getCanonicalName());
                            IOC.register(createAsyncOpKey, new CreateNewInstanceStrategy(
                                    (args) -> {
                                        try {
                                            return new CreateAsyncOperationActor((IObject) args[0]);
                                        } catch (Exception e) {
                                            throw new RuntimeException(e);
                                        }
                                    }));
                        } catch (ResolutionException e) {
                            throw new ActionExecutionException("CreateAsyncOperationActor plugin can't load: can't get CreateAsyncOperationActor key", e);
                        } catch (InvalidArgumentException e) {
                            throw new ActionExecutionException("CreateAsyncOperationActor plugin can't load: can't create strategy", e);
                        } catch (RegistrationException e) {
                            throw new ActionExecutionException("CreateAsyncOperationActor plugin can't load: can't register new strategy", e);
                        }
                    });
            bootstrap.add(item);
        } catch (Exception e) {
            throw new PluginException("Can't load CreateAsyncOperationActor plugin", e);
        }
    }
}
