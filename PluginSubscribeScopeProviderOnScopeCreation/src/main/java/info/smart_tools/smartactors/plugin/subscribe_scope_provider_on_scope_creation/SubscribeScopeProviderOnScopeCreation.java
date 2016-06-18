package info.smart_tools.smartactors.plugin.subscribe_scope_provider_on_scope_creation;

import info.smart_tools.smartactors.core.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.core.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.core.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.iplugin.IPlugin;
import info.smart_tools.smartactors.core.iplugin.exception.PluginException;
import info.smart_tools.smartactors.core.scope_provider.ScopeProvider;
import info.smart_tools.smartactors.core.strategy_container.StrategyContainer;

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
                                } catch (Exception e) {
                                    throw new RuntimeException("Subscribe on scope creation has been failed.", e);
                                }
                            }
                    );
            this.bootstrap.add(item);
        } catch (Throwable e) {
            throw new PluginException("Load plugin execution has been failed.", e);
        }
    }
}
