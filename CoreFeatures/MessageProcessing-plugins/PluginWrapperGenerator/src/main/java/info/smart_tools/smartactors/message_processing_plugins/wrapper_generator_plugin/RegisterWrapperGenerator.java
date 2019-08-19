package info.smart_tools.smartactors.message_processing_plugins.wrapper_generator_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing.wrapper_generator.WrapperGenerator;
import info.smart_tools.smartactors.message_processing_interfaces.iwrapper_generator.IWrapperGenerator;

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
                                    IWrapperGenerator rg = new WrapperGenerator();
                                    IOC.register(
                                            IOC.resolve(
                                                    IOC.getKeyForKeyByNameStrategy(),
                                                    IWrapperGenerator.class.getCanonicalName()
                                            ),
                                            new SingletonStrategy(rg)
                                    );
                                } catch (ResolutionException e) {
                                    throw new ActionExecutionException("RegisterWrapperGenerator plugin can't load: can't get RegisterWrapperGenerator key", e);
                                } catch (InvalidArgumentException e) {
                                    throw new ActionExecutionException("RegisterWrapperGenerator plugin can't load: can't create strategy", e);
                                } catch (RegistrationException e) {
                                    throw new ActionExecutionException("RegisterWrapperGenerator plugin can't load: can't register new strategy", e);
                                }
                    })
                    .revertProcess(() -> {
                        String[] itemNames = { IWrapperGenerator.class.getCanonicalName() };
                        Keys.unregisterByNames(itemNames);
                    });
            this.bootstrap.add(item);
        } catch (Throwable e) {
            throw new PluginException("Could not load 'ReceiverGenerator plugin'", e);
        }
    }
}
