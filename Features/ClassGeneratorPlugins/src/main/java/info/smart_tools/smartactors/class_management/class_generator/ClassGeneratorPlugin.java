package info.smart_tools.smartactors.class_management.class_generator;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.class_management.class_generator_with_java_compile_api.FromStringClassGenerator;
import info.smart_tools.smartactors.class_management.class_generator_with_java_compile_api.class_builder.ClassBuilder;
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
 * Plugin registers needed strategies to the IOC
 */
public class ClassGeneratorPlugin implements IPlugin {

    private final IBootstrap<IBootstrapItem<String>> bootstrap;

    /**
     * The constructor.
     * @param bootstrap the bootstrap
     * @throws Exception if any error occurs
     */
    public ClassGeneratorPlugin(final IBootstrap<IBootstrapItem<String>> bootstrap) throws Exception {
        this.bootstrap = bootstrap;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void load() throws PluginException {
        try {
            IBootstrapItem<String> item = new BootstrapItem("class_generator");

            item
                    .after("IOC")
                    .before("read_initial_config")
                    .process(() -> {
                        try {
                            IOC.register(
                                    Keys.getKeyByName("class-generator:from-string"),
                                    new ApplyFunctionToArgumentsStrategy((args) -> new FromStringClassGenerator()
                            ));
                            IOC.register(
                                    Keys.getKeyByName("class-builder:from_string"),
                                    new ApplyFunctionToArgumentsStrategy(
                                            (args) -> new ClassBuilder((String) args[0], (String) args[1])
                                    )
                            );
                        } catch (ResolutionException | InvalidArgumentException | RegistrationException e ) {
                            throw new ActionExecutionException(e);
                        }
                    })
                    .revertProcess(() -> {
                        String[] keyNames = {
                                "class-generator:from-string",
                                "class-builder:from_string",
                                "class-builder:modifier:public",
                                "class-builder:modifier:private"
                        };
                        Keys.unregisterByNames(keyNames);
                    });
            bootstrap.add(item);
        } catch (InvalidArgumentException e) {
            throw new PluginException(e);
        }
    }
}
