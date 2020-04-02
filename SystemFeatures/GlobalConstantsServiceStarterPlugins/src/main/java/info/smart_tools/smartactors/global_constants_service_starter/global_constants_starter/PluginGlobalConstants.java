package info.smart_tools.smartactors.global_constants_service_starter.global_constants_starter;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.configuration_manager.interfaces.iconfiguration_manager.IConfigurationManager;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing.message_processor.MessageProcessor;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessingSequence;
import info.smart_tools.smartactors.task.interfaces.iqueue.IQueue;
import info.smart_tools.smartactors.task.interfaces.itask.ITask;

/**
 * Plugin that creates and registers global constants object and registers a strategy processing "const" section  of configuration.
 */
public class PluginGlobalConstants implements IPlugin {
    private final IBootstrap<IBootstrapItem<String>> bootstrap;

    /**
     * Constructor
     *
     * @param bootstrap the bootstrap
     */
    public PluginGlobalConstants(final IBootstrap<IBootstrapItem<String>> bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    public void load() throws PluginException {
        try {
            /* "constants_object" - create global constants object */
            IBootstrapItem<String> constantsObjectItem = new BootstrapItem("constants_object");

//            constantsObjectItem.after("IOC");
//            constantsObjectItem.before("starter");
//            constantsObjectItem.after("iobject");
            constantsObjectItem.process(() -> {
                try {
                    IObject obj = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"));
                    IOC.register(Keys.getKeyByName("global constants"), new SingletonStrategy(obj));

                    IOC.register(
                            Keys.getKeyByName("info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor"),
                            new ApplyFunctionToArgumentsStrategy(args -> {
                                IQueue<ITask> taskQueue = (IQueue<ITask>) args[0];
                                IMessageProcessingSequence sequence = (IMessageProcessingSequence) args[1];
                                IObject config;

                                if (args.length > 2) {
                                    config = (IObject) args[2];
                                } else {
                                    try {
                                        config = IOC.resolve(Keys.getKeyByName("global constants"));;
                                    } catch (ResolutionException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                                try {
                                    return new MessageProcessor(taskQueue, sequence, config);
                                } catch (InvalidArgumentException | ResolutionException e) {
                                    throw new RuntimeException(e);
                                }
                            }));
                } catch (ResolutionException | InvalidArgumentException | RegistrationException e) {
                    throw new ActionExecutionException(e);
                }
            });

            bootstrap.add(constantsObjectItem);

            /* "config_section:const" - register strategy processing "const" section of config */
            IBootstrapItem<String> constantsSectionItem = new BootstrapItem("config_section:const");

            constantsSectionItem.after("constants_object");
//            constantsSectionItem.after("config_sections:start");
//            constantsSectionItem.before("config_sections:done");
            constantsSectionItem.process(() -> {
                try {
                    IConfigurationManager configurationManager = IOC.resolve(Keys.getKeyByName(IConfigurationManager.class.getCanonicalName()));
                    configurationManager.addSectionStrategy(new ConstantsSectionStrategy());
                } catch (ResolutionException | InvalidArgumentException e) {
                    throw new ActionExecutionException(e);
                }
            });

            bootstrap.add(constantsSectionItem);
        } catch (InvalidArgumentException e) {
            throw new PluginException(e);
        }
    }
}
