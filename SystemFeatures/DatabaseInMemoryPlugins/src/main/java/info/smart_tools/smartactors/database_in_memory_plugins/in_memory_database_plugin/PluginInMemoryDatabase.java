package info.smart_tools.smartactors.database_in_memory_plugins.in_memory_database_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.database_in_memory.in_memory_database.InMemoryDatabaseIOCInitializer;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;

/**
 * Plugin for in memory database
 */
public class PluginInMemoryDatabase implements IPlugin {

    private final IBootstrap<IBootstrapItem<String>> bootstrap;

    /**
     * Constructor
     *
     * @param bootstrap bootstrap element
     */
    public PluginInMemoryDatabase(final IBootstrap<IBootstrapItem<String>> bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    public void load() throws PluginException {
        try {
            BootstrapItem item = new BootstrapItem("InMemoryDatabase");
            item
//                    .after("IOC")
//                    .after("IFieldNamePlugin")
                    .process(() -> {
                                try {
                                    InMemoryDatabaseIOCInitializer.init();
                                } catch (Exception e) {
                                    throw new ActionExecutionException("Failed to load plugin \"NestedFieldName\"", e);
                                }
                            }
                    );
            bootstrap.add(item);
        } catch (InvalidArgumentException e) {
            throw new PluginException(e);
        }
    }

}
