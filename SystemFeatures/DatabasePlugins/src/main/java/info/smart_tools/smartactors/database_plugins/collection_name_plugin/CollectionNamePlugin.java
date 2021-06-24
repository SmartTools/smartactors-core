package info.smart_tools.smartactors.database_plugins.collection_name_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.database.database_storage.exceptions.StorageException;
import info.smart_tools.smartactors.database.database_storage.utils.CollectionName;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.ioc.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.ioc.resolve_by_name_ioc_with_lambda_strategy.ResolveByNameIocStrategy;

/**
 * Plugin for load IOC strategy for collection name object
 */
public class CollectionNamePlugin implements IPlugin {

    private final IBootstrap<IBootstrapItem<String>> bootstrap;

    /**
     * Constructor
     * @param bootstrap bootstrap element
     */
    public CollectionNamePlugin(final IBootstrap<IBootstrapItem<String>> bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    public void load() throws PluginException {

        try {
            IBootstrapItem<String> item = new BootstrapItem("CollectionNamePlugin");
            item
                .after("core")
//                .after("IOC")
//                .before("starter")
                .process(() -> {
                    try {
                        IKey collectionNameKey = Keys.getKeyByName(CollectionName.class.getCanonicalName());
                        IOC.register(collectionNameKey, new ResolveByNameIocStrategy(
                            (args) -> {
                                String name = String.valueOf(args[0]);
                                try {
                                    return CollectionName.fromString(name);
                                } catch (StorageException e) {
                                    throw new RuntimeException("Can't resolve collection name: ", e);
                                }
                            }));
                    } catch (ResolutionException e) {
                        throw new ActionExecutionException("CollectionName plugin can't load: can't get CollectionName key", e);
                    } catch (InvalidArgumentException e) {
                        throw new ActionExecutionException("CollectionName plugin can't load: can't create strategy", e);
                    } catch (RegistrationException e) {
                        throw new ActionExecutionException("CollectionName plugin can't load: can't register new strategy", e);
                    }
                });
            bootstrap.add(item);
        } catch (InvalidArgumentException e) {
            throw new PluginException("Can't load collection name plugin", e);
        }
    }
}
