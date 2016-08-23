package info.smart_tools.smartactors.plugin.chain_testing.assertions;

import info.smart_tools.smartactors.core.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.core.chain_testing.assertions.EqualAssertion;
import info.smart_tools.smartactors.core.chain_testing.assertions.NotEqualAssertion;
import info.smart_tools.smartactors.core.iaction.exception.ActionExecuteException;
import info.smart_tools.smartactors.core.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.core.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.core.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.iplugin.IPlugin;
import info.smart_tools.smartactors.core.iplugin.exception.PluginException;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.core.singleton_strategy.SingletonStrategy;

/**
 * Plugin registering some assertions for chain tests.
 */
public class PluginChainTestingAssertions implements IPlugin {
    private final IBootstrap<IBootstrapItem<String>> bootstrap;

    /**
     * Constructor
     * @param bootstrap the bootstrap
     */
    public PluginChainTestingAssertions(final IBootstrap<IBootstrapItem<String>> bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    public void load() throws PluginException {
        try {
            IBootstrapItem<String> assertionsItem = new BootstrapItem("chain_testing_assertions");

            assertionsItem
                    .after("IOC")
                    .before("configure")
                    .process(() -> {
                        try {
                            IOC.register(Keys.getOrAdd("assertion of type equal"), new SingletonStrategy(new EqualAssertion()));
                            IOC.register(Keys.getOrAdd("assertion of type not equal"), new SingletonStrategy(new NotEqualAssertion()));
                        } catch (ResolutionException | RegistrationException | InvalidArgumentException e) {
                            throw new ActionExecuteException(e);
                        }
                    });

            bootstrap.add(assertionsItem);
        } catch (InvalidArgumentException e) {
            throw new PluginException(e);
        }
    }
}
