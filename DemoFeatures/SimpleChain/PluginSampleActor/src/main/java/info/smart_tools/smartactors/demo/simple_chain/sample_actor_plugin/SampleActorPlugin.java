package info.smart_tools.smartactors.demo.simple_chain.sample_actor_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.demo.simple_chain.sample_actor.SampleActor;
import info.smart_tools.smartactors.demo.simple_chain.sample_other_actor.SampleOtherActor;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.ioc.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;

/**
 * Plugin for register {@link SampleActor} creation strategy
 */
public class SampleActorPlugin implements IPlugin {

    private final IBootstrap<IBootstrapItem<String>> bootstrap;

    public SampleActorPlugin(final IBootstrap<IBootstrapItem<String>> bootstrap) {
        this.bootstrap = bootstrap;
    }

    /**
     * Load the plugin for {@link SampleActor}
     * @throws PluginException Throw when plugin can't be load
     */
    @Override
    public void load() throws PluginException {
        try {
            IBootstrapItem<String> item1 = new BootstrapItem("SampleActorPlugin");

            item1
                    .process(() -> {
                try {
                    IOC.register(Keys.getKeyByName("SampleActor"), new ApplyFunctionToArgumentsStrategy(
                            (args) -> {
                                try {
                                    return new SampleActor();
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            }));
                } catch (ResolutionException | RegistrationException | InvalidArgumentException e) {
                    throw new ActionExecutionException(e);
                }
            });
            bootstrap.add(item1);

            IBootstrapItem<String> item2 = new BootstrapItem("SampleOtherActorPlugin");

            item2
                    .process(() -> {
                        try {
                            IOC.register(Keys.getKeyByName("SampleOtherActor"), new ApplyFunctionToArgumentsStrategy(
                                    (args) -> {
                                        try {
                                            return new SampleOtherActor();
                                        } catch (Exception e) {
                                            throw new RuntimeException(e);
                                        }
                                    }));
                        } catch (ResolutionException | RegistrationException | InvalidArgumentException e) {
                            throw new ActionExecutionException(e);
                        }
                    });
            bootstrap.add(item2);

        } catch (InvalidArgumentException e) {
            throw new PluginException("Can't load SampleActor plugin", e);
        }
    }
}
