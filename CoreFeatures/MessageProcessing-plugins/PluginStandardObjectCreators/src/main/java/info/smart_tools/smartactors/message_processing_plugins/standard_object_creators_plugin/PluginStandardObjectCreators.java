package info.smart_tools.smartactors.message_processing_plugins.standard_object_creators_plugin;

import info.smart_tools.smartactors.feature_loading_system.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecuteException;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.message_processing_interfaces.iroutable_object_creator.IRoutedObjectCreator;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;

/**
 *
 */
public class PluginStandardObjectCreators implements IPlugin {
    private IBootstrap<IBootstrapItem<String>> bootstrap;

    /**
     * The constructor.
     *
     * @param bootstrap    the bootstrap
     */
    public PluginStandardObjectCreators(final IBootstrap<IBootstrapItem<String>> bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    public void load() throws PluginException {
        try {
            BootstrapItem creatorsItem = new BootstrapItem("standard_object_creators");

            creatorsItem
                    .after("IOC")
                    .after("IFieldNamePlugin")
                    .before("starter")
                    .process(() -> {
                        try {
                            IOC.register(
                                    Keys.getOrAdd(IRoutedObjectCreator.class.getCanonicalName() + "#raw"),
                                    new SingletonStrategy(new RawObjectCreator()));
                        } catch (ResolutionException e) {
                            throw new ActionExecuteException("StandardObjectCreators plugin can't load: can't get StandardObjectCreators key", e);
                        } catch (InvalidArgumentException e) {
                            throw new ActionExecuteException("StandardObjectCreators plugin can't load: can't create strategy", e);
                        } catch (RegistrationException e) {
                            throw new ActionExecuteException("StandardObjectCreators plugin can't load: can't register new strategy", e);
                        }
                    });

            bootstrap.add(creatorsItem);
        } catch (InvalidArgumentException e) {
            throw new PluginException(e);
        }
    }
}
