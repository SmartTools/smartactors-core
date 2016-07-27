package info.smart_tools.smartactors.plugin.collection_name;

import info.smart_tools.smartactors.core.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.core.db_storage.exceptions.StorageException;
import info.smart_tools.smartactors.core.db_storage.utils.CollectionName;
import info.smart_tools.smartactors.core.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.core.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.core.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.iplugin.IPlugin;
import info.smart_tools.smartactors.core.iplugin.exception.PluginException;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.core.resolve_by_name_ioc_with_lambda_strategy.ResolveByNameIocStrategy;

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
                .after("IOC")
                .process(() -> {
                    try {
                        IKey collectionNameKey = Keys.getOrAdd(CollectionName.class.toString());
                        IOC.register(collectionNameKey, new ResolveByNameIocStrategy(
                            (args) -> {
                                String name = String.valueOf(args[0]);
                                try {
                                    return CollectionName.fromString(name);
                                } catch (StorageException e) {
                                    throw new RuntimeException("Can't resolve collection name: ", e);
                                }
                            }));
                    } catch (RegistrationException | InvalidArgumentException | ResolutionException e) {
                        throw new RuntimeException(e);
                    }
                });
            bootstrap.add(item);
        } catch (InvalidArgumentException e) {
            throw new PluginException("Can't load collection name plugin", e);
        }
    }
}
