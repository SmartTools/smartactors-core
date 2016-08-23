package info.smart_tools.smartactors.plugin.chain_testing;

import info.smart_tools.smartactors.core.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.core.chain_testing.MainTestChain;
import info.smart_tools.smartactors.core.chain_testing.TestRunner;
import info.smart_tools.smartactors.core.chain_testing.section_strategy.TestsSectionStrategy;
import info.smart_tools.smartactors.core.create_new_instance_strategy.CreateNewInstanceStrategy;
import info.smart_tools.smartactors.core.iaction.IAction;
import info.smart_tools.smartactors.core.iaction.exception.ActionExecuteException;
import info.smart_tools.smartactors.core.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.core.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.core.iconfiguration_manager.IConfigurationManager;
import info.smart_tools.smartactors.core.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.iplugin.IPlugin;
import info.smart_tools.smartactors.core.iplugin.exception.PluginException;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.core.singleton_strategy.SingletonStrategy;

/**
 * Plugin that registers strategy processing "tests" section of configuration and some related components.
 */
public class PluginChainTesting implements IPlugin {
    private final IBootstrap<IBootstrapItem<String>> bootstrap;

    /**
     * Constructor
     * @param bootstrap the bootstrap
     */
    public PluginChainTesting(final IBootstrap<IBootstrapItem<String>> bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    public void load() throws PluginException {
        try {
            IBootstrapItem<String> runnerItem = new BootstrapItem("chain_tests_runner");

            runnerItem
                    .after("IFieldNamePlugin")
                    .process(() -> {
                        try {
                            IOC.register(Keys.getOrAdd(TestRunner.class.getCanonicalName()), new SingletonStrategy(new TestRunner()));
                        } catch (ResolutionException | RegistrationException | InvalidArgumentException e) {
                            throw new ActionExecuteException(e);
                        }
                    });

            bootstrap.add(runnerItem);

            IBootstrapItem<String> strategyItem = new BootstrapItem("config_section:tests");

            strategyItem
                    .after("config_section:executor")
                    .after("config_section:maps")
                    .after("config_section:objects")
                    .after("message_processor")
                    .after("message_processing_sequence")
                    .after("main_test_chain")
                    .after("chain_tests_runner")
                    .before("configure")
                    .process(() -> {
                        try {
                            IConfigurationManager configurationManager =
                                    IOC.resolve(Keys.getOrAdd(IConfigurationManager.class.getCanonicalName()));

                            configurationManager.addSectionStrategy(new TestsSectionStrategy());
                        } catch (ResolutionException | InvalidArgumentException e) {
                            throw new ActionExecuteException(e);
                        }
                    });

            bootstrap.add(strategyItem);

            IBootstrapItem<String> mainChainItem = new BootstrapItem("main_test_chain");

            mainChainItem
                    .after("IOC")
                    .process(() -> {
                        try {
                            IOC.register(Keys.getOrAdd(MainTestChain.class.getCanonicalName()),
                                    new CreateNewInstanceStrategy(args -> {
                                        try {
                                            return new MainTestChain((IAction) args[0], (IObject) args[1]);
                                        } catch (InvalidArgumentException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }));
                        } catch (ResolutionException | RegistrationException | InvalidArgumentException e) {
                            throw new ActionExecuteException(e);
                        }
                    });

            bootstrap.add(mainChainItem);

        } catch (InvalidArgumentException e) {
            throw new PluginException(e);
        }
    }
}
