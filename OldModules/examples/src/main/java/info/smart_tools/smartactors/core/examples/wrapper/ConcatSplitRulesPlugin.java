package info.smart_tools.smartactors.core.examples.wrapper;

import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.interfaces.istrategy.exception.StrategyException;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

/**
 * Example of plugin to register transformation strategies for Wrapper.
 */
public class ConcatSplitRulesPlugin implements IPlugin {

    private final IBootstrap<IBootstrapItem<String>> bootstrap;

    /**
     * Creates the plugin.
     * @param bootstrap bootstrap where this plugin puts {@link IBootstrapItem}.
     */
    public ConcatSplitRulesPlugin(final IBootstrap<IBootstrapItem<String>> bootstrap) {   // this constructor signature is required
        this.bootstrap = bootstrap;
    }

    @Override
    public void load() throws PluginException {
        try {
            IBootstrapItem<String> item = new BootstrapItem("ConcalSplitRulesPlugin");
            item.after("IOC");
            item.after("ioc_keys");
            item.process(() -> {
                try {
                    registerConcatRule();
                    registerSplitRule();
                } catch (Exception e) {
                    throw new RuntimeException("Failed to register strategies in ConcatSplitRulesPlugin", e);
                }
            });
            bootstrap.add(item);
        } catch (Exception e) {
            throw new PluginException("Failed to load ConcatSplitRulesPlugin", e);
        }
    }

    private void registerConcatRule() throws ResolutionException, RegistrationException {
        //call IOC.resolve for put "concat_strategy" into cache of ResolveByNameDependency strategy
        IKey key = Keys.getKeyByName(IStrategy.class.getCanonicalName());
        String name = "concat_strategy";
        IStrategy strategy = new IStrategy() {
            @Override
            public <T> T resolve(final Object... args) throws StrategyException {
                String result = Arrays.stream(args).map(String::valueOf).collect(Collectors.joining());
                return (T) result;
            }
        };
        IOC.resolve(key, name, strategy);
    }

    private void registerSplitRule() throws ResolutionException, RegistrationException {
        //call IOC.resolve for put "concat_strategy" into cache of ResolveByNameDependency strategy
        IKey key = Keys.getKeyByName(IStrategy.class.getCanonicalName());
        String name = "split_strategy";
        IStrategy strategy = new IStrategy() {
            @Override
            public <T> T resolve(final Object... args) throws StrategyException {
                String value = (String) args[0];
                String delimiters = (String) args[1];
                List<String> result = new ArrayList<>();
                StringTokenizer tokenizer = new StringTokenizer(value, delimiters, false);
                while (tokenizer.hasMoreTokens()) {
                    result.add(tokenizer.nextToken());
                }
                return (T) result;
            }
        };
        IOC.resolve(key, name, strategy);
    }

}
