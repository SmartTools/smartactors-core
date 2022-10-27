package info.smart_tools.smartactors.message_processing_plugins.exception_handling_receiver_plugins;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.ioc.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing.exception_handling_receivers.RetryingToTakeResourceExceptionHandler;

/**
 * Plugin that registers RetryingToTakeResourceExceptionHandler.
 */
public class PluginRetryingToTakeResourceExceptionHandler extends BootstrapPlugin {

    /**
     * Constructor
     * @param bootstrap the bootstrap item
     */
    public PluginRetryingToTakeResourceExceptionHandler(final IBootstrap bootstrap) {
        super(bootstrap);
    }

    /**
     * Register RetryingToTakeResourceExceptionHandler as a singleton.
     *
     * @throws ResolutionException if error occurs resoling key or dependencies of chain choice strategy
     * @throws RegistrationException if error occurs registering the strategy
     * @throws InvalidArgumentException if {@link SingletonStrategy} doesn't like our arguments
     */
    @BootstrapPlugin.Item("PluginRetryingToTakeResourceExceptionHandler")
    @BootstrapPlugin.After({
            "IOC",
            "IFieldNamePlugin",
    })
    public void item()
            throws ResolutionException, RegistrationException, InvalidArgumentException {
        IOC.register(Keys.getKeyByName("RetryingToTakeResourceExceptionHandler"),
                new SingletonStrategy(new RetryingToTakeResourceExceptionHandler()));
    }

    /**
     * Unregisters RetryingToTakeResourceExceptionHandler.
     *
     */
    @BootstrapPlugin.ItemRevert("PluginRetryingToTakeResourceExceptionHandler")
    public void revertItem() {
        String[] keyNames = { "RetryingToTakeResourceExceptionHandler" };
        Keys.unregisterByNames(keyNames);
    }
}
