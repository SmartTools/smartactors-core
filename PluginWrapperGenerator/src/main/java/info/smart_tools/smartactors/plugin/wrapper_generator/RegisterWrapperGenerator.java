package info.smart_tools.smartactors.plugin.wrapper_generator;

import info.smart_tools.smartactors.core.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.core.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.core.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.iplugin.IPlugin;
import info.smart_tools.smartactors.core.iplugin.exception.PluginException;
import info.smart_tools.smartactors.core.iwrapper_generator.IWrapperGenerator;
import info.smart_tools.smartactors.core.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.core.wrapper_generator.WrapperGenerator;

/**
 * Plugin creates instance of {@link WrapperGenerator} and registers it into IOC,
 */
public class RegisterWrapperGenerator implements IPlugin {

    /** Local storage for instance of {@link IBootstrap}*/
    private IBootstrap<IBootstrapItem<String>> bootstrap;

    /**
     * Constructor with single argument
     * @param bootstrap instance of {@link IBootstrap}
     * @throws InvalidArgumentException if any errors occurred
     */
    public RegisterWrapperGenerator(final IBootstrap<IBootstrapItem<String>> bootstrap)
            throws InvalidArgumentException {
        if (null == bootstrap) {
            throw new InvalidArgumentException("Incoming argument should not be null.");
        }
        this.bootstrap = bootstrap;
    }

    @Override
    public void load() throws PluginException {
        try {
            IBootstrapItem<String> item = new BootstrapItem("InitializeWrapperGenerator");
            item
                    .after("IOC")
                    .process(
                            () -> {
                                try {
                                    IWrapperGenerator rg = new WrapperGenerator(this.getClass().getClassLoader());
                                    IOC.register(
                                            IOC.resolve(
                                                    IOC.getKeyForKeyStorage(),
                                                    WrapperGenerator.class.getCanonicalName()
                                            ),
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
