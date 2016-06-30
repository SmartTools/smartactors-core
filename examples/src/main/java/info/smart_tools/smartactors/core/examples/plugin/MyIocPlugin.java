package info.smart_tools.smartactors.core.examples.plugin;

import info.smart_tools.smartactors.core.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.core.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.core.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.iplugin.IPlugin;
import info.smart_tools.smartactors.core.iplugin.exception.PluginException;
import info.smart_tools.smartactors.core.iscope.IScope;
import info.smart_tools.smartactors.core.resolve_by_name_ioc_with_lambda_strategy.ResolveByNameIocStrategy;
import info.smart_tools.smartactors.core.scope_provider.ScopeProvider;
import info.smart_tools.smartactors.core.strategy_container.StrategyContainer;
import info.smart_tools.smartactors.core.string_ioc_key.Key;

/**
 *  The plugin which mimics the IOC initialization and provides "IOC" dependency.
 */
public class MyIocPlugin implements IPlugin {

    private final IBootstrap<IBootstrapItem<String>> bootstrap;

    public MyIocPlugin(final IBootstrap<IBootstrapItem<String>> bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    public void load() throws PluginException {
        try {
            IBootstrapItem<String> item = new BootstrapItem("IOC");
            item.process(() -> {
                try {
                    Object scopeKey = ScopeProvider.createScope(null);
                    IScope scope = ScopeProvider.getScope(scopeKey);
                    ScopeProvider.setCurrentScope(scope);
                    scope.setValue(IOC.getIocKey(), new StrategyContainer());
                    IOC.register(IOC.getKeyForKeyStorage(), new ResolveByNameIocStrategy(
                            (a) -> {
                                try {
                                    return new Key((String) a[0]);
                                } catch (InvalidArgumentException e) {
                                    throw new RuntimeException(e);
                                }
                            })
                    );
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                System.out.println("IOC initialized");
            });
            bootstrap.add(item);
        } catch (Exception e) {
            throw new PluginException("Could not load IOC plugin", e);
        }
        System.out.println("IOC loaded");
    }

}
