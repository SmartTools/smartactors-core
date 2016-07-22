package info.smart_tools.smartactors.plugin.receiver_generator;

import info.smart_tools.smartactors.core.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.core.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.core.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.iplugin.IPlugin;
import info.smart_tools.smartactors.core.iplugin.exception.PluginException;
import info.smart_tools.smartactors.core.ireceiver_generator.IReceiverGenerator;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.core.receiver_generator.ReceiverGenerator;
import info.smart_tools.smartactors.core.singleton_strategy.SingletonStrategy;

/**
 * Plugin creates instance of {@link ReceiverGenerator} and register its into IOC,
 * adds some needed strategies for correctly work of {@link ReceiverGenerator}.
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
                                    IReceiverGenerator rg = new ReceiverGenerator(null);
                                    IOC.register(
                                            Keys.getOrAdd(ReceiverGenerator.class.getCanonicalName()),
                                            new SingletonStrategy(rg)
                                    );
                                } catch (Exception e) {
                                    throw new RuntimeException("Could not initialize receiver generator.", e);
                                }
                            }
                        );
            this.bootstrap.add(item);
        } catch (Throwable e) {
            throw new PluginException("Could not load 'ReceiverGenerator plugin'", e);
        }
    }
}
