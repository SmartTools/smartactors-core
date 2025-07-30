package info.smart_tools.smartactors.message_processing_plugins.chain_modification_strategies_plugin;

import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.ioc.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing.chain_modifications.ReplaceReceiversChainModificationStrategy;

/**
 *
 */
public class ChainModificationStrategiesPlugin extends BootstrapPlugin {
    /**
     * The constructor.
     *
     * @param bootstrap the bootstrap
     */
    public ChainModificationStrategiesPlugin(final IBootstrap bootstrap) {
        super(bootstrap);
    }

    /**
     * Register {@link ReplaceReceiversChainModificationStrategy}.
     *
     * @throws ResolutionException if error occurs resolving key or strategy dependencies
     * @throws RegistrationException if error occurs registering the strategy
     */
    @Item("chain_modification_strategies:replace_receivers")
    @After({
            "IFieldNamePlugin",
    })
    public void registerReceiverReplaceModification()
            throws ResolutionException, RegistrationException {
        IOC.register(Keys.getKeyByName("chain modification: replace receivers"), new ReplaceReceiversChainModificationStrategy());
    }

    /**
     * Unregisters {@link ReplaceReceiversChainModificationStrategy}.
     */
    @ItemRevert("chain_modification_strategies:replace_receivers")
    public void unregisterReceiverReplaceModification() {
        String[] keyNames = { "chain modification: replace receivers" };
        Keys.unregisterByNames(keyNames);
    }
}
