package info.smart_tools.smartactors.message_processing_plugins.receiver_generator_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
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
import info.smart_tools.smartactors.message_processing.receiver_generator.ReceiverGenerator;
import info.smart_tools.smartactors.message_processing_interfaces.ireceiver_generator.IReceiverGenerator;

/**
 * Plugin creates instance of {@link ReceiverGenerator} and registers it into IOC.
 */
public class InitializeReceiverGenerator implements IPlugin {

    /** Local storage for instance of {@link IBootstrap}*/
    private IBootstrap<IBootstrapItem<String>> bootstrap;

    /**
     * Constructor with single argument
     * @param bootstrap instance of {@link IBootstrap}
     * @throws InvalidArgumentException if any errors occurred
     */
    public InitializeReceiverGenerator(final IBootstrap<IBootstrapItem<String>> bootstrap)
            throws InvalidArgumentException {
        if (null == bootstrap) {
            throw new InvalidArgumentException("Incoming argument should not be null.");
        }
        this.bootstrap = bootstrap;
    }

    @Override
    public void load() throws PluginException {
        try {
            IBootstrapItem<String> item = new BootstrapItem("InitializeReceiverGenerator");
            item
                    .after("IOC")
                    .process(
                            () -> {
                                try {
                                    IReceiverGenerator rg = new ReceiverGenerator();
                                    IOC.register(
                                            Keys.getKeyByName(IReceiverGenerator.class.getCanonicalName()),
                                            new SingletonStrategy(rg)
                                    );
                                } catch (ResolutionException e) {
                                    throw new ActionExecutionException("InitializeReceiverGenerator plugin can't load: can't get InitializeReceiverGenerator key", e);
                                } catch (InvalidArgumentException e) {
                                    throw new ActionExecutionException("InitializeReceiverGenerator plugin can't load: can't create strategy", e);
                                } catch (RegistrationException e) {
                                    throw new ActionExecutionException("InitializeReceiverGenerator plugin can't load: can't register new strategy", e);
                                }
                            }
                        )
                    .revertProcess(
                            () -> {
                                String[] itemNames = { IReceiverGenerator.class.getCanonicalName() };
                                Keys.unregisterByNames(itemNames);
                            }
                    );
            this.bootstrap.add(item);
        } catch (Throwable e) {
            throw new PluginException("Could not load 'ReceiverGenerator plugin'", e);
        }
    }
}
