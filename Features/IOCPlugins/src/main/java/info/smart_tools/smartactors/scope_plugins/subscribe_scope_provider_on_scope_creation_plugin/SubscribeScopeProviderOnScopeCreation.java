package info.smart_tools.smartactors.scope_plugins.subscribe_scope_provider_on_scope_creation_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.strategy_container.StrategyContainer;
import info.smart_tools.smartactors.scope.iscope_provider_container.exception.ScopeProviderException;
import info.smart_tools.smartactors.scope.scope_provider.ScopeProvider;

/**
 * Plugin.
 * Implements {@link IPlugin}
 * Subscribe ScopeProvider on a Scope creation.
 */
public class SubscribeScopeProviderOnScopeCreation implements IPlugin {

    /** Local storage for instance of {@link IBootstrap}*/
    private IBootstrap<IBootstrapItem<String>> bootstrap;

    /**
     * Constructor with single argument
     * @param bootstrap instance of {@link IBootstrap}
     * @throws InvalidArgumentException if any errors occurred
     */
    public SubscribeScopeProviderOnScopeCreation(final IBootstrap<IBootstrapItem<String>> bootstrap)
            throws InvalidArgumentException {
        if (null == bootstrap) {
            throw new InvalidArgumentException("Incoming argument should not be null.");
        }
        this.bootstrap = bootstrap;
    }

    @Override
    public void load()
            throws PluginException {
        try {
            IBootstrapItem<String> item = new BootstrapItem("SubscribeScopeProviderOnScopeCreation");
            item
                    .before("CreateNewScope")
                    .process(
                            () -> {
                                try {
                                    ScopeProvider.subscribeOnCreationNewScope(
                                            scope -> {
                                                try {
                                                    scope.setValue(IOC.getIocKey(), new StrategyContainer());
                                                } catch (Exception e) {
                                                    throw new Error(e);
                                                }
                                            }
                                    );
                                } catch (ScopeProviderException e) {
                                    throw new ActionExecutionException("SubscribeScopeProviderOnScopeCreation plugin can't load: can't subscribe on creation new scope", e);
                                }
                            }
                    );
            this.bootstrap.add(item);
        } catch (InvalidArgumentException e) {
            throw new PluginException("Load plugin execution has been failed.", e);
        }
    }
}
