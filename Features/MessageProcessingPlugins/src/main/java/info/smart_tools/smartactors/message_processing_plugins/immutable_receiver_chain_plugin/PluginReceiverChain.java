package info.smart_tools.smartactors.message_processing_plugins.immutable_receiver_chain_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.ioc.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing.receiver_chain.ImmutableReceiverChainStrategy;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IReceiverChain;

/**
 *
 */
public class PluginReceiverChain implements IPlugin {
    private final IBootstrap<IBootstrapItem<String>> bootstrap;

    /**
     * The constructor.
     *
     * @param bootstrap    the bootstrap
     */
    public PluginReceiverChain(final IBootstrap<IBootstrapItem<String>> bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    public void load() throws PluginException {
        try {
            BootstrapItem receiverChainItem = new BootstrapItem("receiver_chain");

            receiverChainItem
                    .after("IOC")
                    .process(() -> {
                        try {
                            IOC.register(
                                    Keys.getKeyByName(IReceiverChain.class.getCanonicalName()),
                                    new ImmutableReceiverChainStrategy());
                        } catch (ResolutionException e) {
                            throw new ActionExecutionException("ReceiverChain plugin can't load: can't get ReceiverChain key", e);
                        } catch (RegistrationException e) {
                            throw new ActionExecutionException("ReceiverChain plugin can't load: can't register new strategy", e);
                        }
                    })
                    .revertProcess(() -> {
                        String[] keyNames = { IReceiverChain.class.getCanonicalName() };
                        Keys.unregisterByNames(keyNames);
                    });

            bootstrap.add(receiverChainItem);
        } catch (InvalidArgumentException e) {
            throw new PluginException(e);
        }
    }
}
