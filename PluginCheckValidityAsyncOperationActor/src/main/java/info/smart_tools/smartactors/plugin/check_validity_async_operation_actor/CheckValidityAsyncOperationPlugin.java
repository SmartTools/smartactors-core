package info.smart_tools.smartactors.plugin.check_validity_async_operation_actor;

import info.smart_tools.smartactors.core.actors.check_validity_async_operation.CheckValidityAsyncOperationActor;
import info.smart_tools.smartactors.core.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.core.create_new_instance_strategy.CreateNewInstanceStrategy;
import info.smart_tools.smartactors.core.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.core.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.core.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.iplugin.IPlugin;
import info.smart_tools.smartactors.core.iplugin.exception.PluginException;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;

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

            item.process(() -> {
                try {
                    IKey operationKey = Keys.getOrAdd(CheckValidityAsyncOperationActor.class.getCanonicalName());
                    IOC.register(operationKey, new CreateNewInstanceStrategy(
                            (args) -> {
                                return new CheckValidityAsyncOperationActor((IObject) args[0]);
                        }
                    ));
                } catch (RegistrationException | InvalidArgumentException | ResolutionException e) {
                    throw new RuntimeException(e);
                }
            });
            bootstrap.add(item);
        } catch (InvalidArgumentException e) {
            throw new PluginException("Can't load CheckValidityAsyncOperation plugin", e);
        }
    }
}